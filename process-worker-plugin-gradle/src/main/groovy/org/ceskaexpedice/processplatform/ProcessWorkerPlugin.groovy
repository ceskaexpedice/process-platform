package org.ceskaexpedice.processplatform

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class ProcessWorkerExtension {
    String workerName
    Project warProject
	String warArtifact
    List<Project> plugins = []
	String warName = "worker.war"
}

class ProcessWorkerPlugin implements Plugin<Project> {
    void apply(Project project) {
        def ext = project.extensions.create("processWorker", ProcessWorkerExtension)

        project.afterEvaluate {
            if (!ext.workerName) {
                throw new IllegalArgumentException("processWorker.workerName must be set")
            }

	
			// Rozhodovací logika
			if (ext.warProject && ext.warArtifact) {
				throw new IllegalArgumentException("processWorker.warProject and processWorker.warArtifact cannot be set at the same time. Please use only one.")
			}


			def warDependency
			if (ext.warProject) {
				// Použití závislosti na projektu
				warDependency = ext.warProject
			} else if (ext.warArtifact) {
				// Použití závislosti na artefaktu z repozitáře
				warDependency = ext.warArtifact
			} else {
				// Žádná z vlastností není nastavena
				throw new IllegalArgumentException("Either processWorker.warProject or processWorker.warArtifact must be set")
			}

            def outputDir = new File(project.buildDir, "worker")
            def webappsDir = new File(outputDir, "webapps")
            def pluginsDir = new File(outputDir, "lib/plugins")

			def warConfig = project.configurations.create("workerWar")
			project.dependencies.add(warConfig.name, warDependency)


            // Build task
            def buildWorkerTask = project.tasks.register("buildWorker") {
                group = "build"
                description = "Assembles worker with WAR and selected plugins"

				if (ext.warProject) {
					// Pokud je nastaven Project, závislost je na úloze 'war' tohoto projektu
					dependsOn ext.warProject.tasks.named("war")
				}

				inputs.files(warConfig)

    
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
                    //def warFile = ext.warProject.tasks.named("war").get().archiveFile.get().asFile

					File warFile
					if (ext.warProject) {
						warFile = ext.warProject.tasks.named("war").get().archiveFile.get().asFile
					} else {
						warFile = warConfig.singleFile
					}
					
					
                    //def warTarget = new File(webappsDir, "process-worker.war")
					def warTarget = new File(webappsDir, ext.warName)
                    warTarget.parentFile.mkdirs()
                    project.copy {
                        from warFile
                        into webappsDir
						rename { fileName -> ext.warName }
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
