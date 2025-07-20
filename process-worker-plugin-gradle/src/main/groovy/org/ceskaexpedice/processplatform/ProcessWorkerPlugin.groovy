package org.ceskaexpedice.processplatform

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class ProcessWorkerExtension {
    String workerName
    Project warProject
    List<Project> plugins = []
}

class ProcessWorkerPlugin implements Plugin<Project> {
    void apply(Project project) {
        def ext = project.extensions.create("processWorker", ProcessWorkerExtension)

        project.afterEvaluate {
            if (!ext.workerName) {
                throw new IllegalArgumentException("processWorker.workerName must be set")
            }

            if (!ext.warProject) {
                throw new IllegalArgumentException("processWorker.warProject must be set")
            }

            def outputDir = new File(project.buildDir, "worker")
            def webappsDir = new File(outputDir, "webapps")
            def pluginsDir = new File(outputDir, "lib/plugins")

            // Build task
            def buildWorkerTask = project.tasks.register("buildWorker") {
                group = "build"
                description = "Assembles worker with WAR and selected plugins"

                // Depends on WAR build
                dependsOn ext.warProject.tasks.named("war")

                // Depends on each plugin's buildProcessPlugin
                ext.plugins.each { pluginProject ->
                    def pluginTask = pluginProject.tasks.findByName("buildProcessPlugin")
                    if (pluginTask == null) {
                        throw new GradleException("Project ${pluginProject.path} does not define task buildProcessPlugin")
                    }
                    dependsOn pluginTask
                }

                doLast {
                    println "Assembling worker '${ext.workerName}'..."

                    // Copy WAR
                    def warFile = ext.warProject.tasks.named("war").get().archiveFile.get().asFile
                    def warTarget = new File(webappsDir, "process-worker.war")
                    warTarget.parentFile.mkdirs()
                    project.copy {
                        from warFile
                        into webappsDir
                    }
                    println "Copied WAR to ${warTarget}"

                    // Copy plugin distributions
                    ext.plugins.each { pluginProject ->
                        def pluginExt = pluginProject.extensions.findByName("processPlugin")
                        def pluginName = pluginExt?.pluginName
                        if (!pluginName) {
                            throw new GradleException("Plugin ${pluginProject.path} does not define processPlugin.pluginName")
                        }

                        def pluginDistDir = new File(pluginProject.buildDir, "distributions/${pluginName}")
                        def targetDir = new File(pluginsDir, pluginName)

                        if (!pluginDistDir.exists()) {
                            throw new GradleException("Missing plugin distribution for ${pluginName} at ${pluginDistDir}")
                        }

                        project.copy {
                            from pluginDistDir
                            into targetDir
                        }
                        println "Copied plugin '${pluginName}' to ${targetDir}"
                    }

                    println "✅ Worker '${ext.workerName}' built at: ${outputDir}"
                }
            }

            // Build includes buildWorker
            project.tasks.named("build").configure {
                dependsOn buildWorkerTask
            }
        }
    }
}
