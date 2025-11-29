package org.ceskaexpedice.processplatform

import groovy.json.JsonOutput
import org.gradle.api.Plugin
import org.gradle.api.Project

class ProcessPluginExtension {
    String pluginName
    List<Map<String, Object>> profiles = []

    String spiInterface
    String spiImplementation
}

class ProcessPlugin implements Plugin<Project> {
    void apply(Project project) {
        def ext = project.extensions.create("processPlugin", ProcessPluginExtension)

        project.afterEvaluate {

            def errors = []

            if (!ext.pluginName) {
                errors << "ProcessPlugin: 'pluginName' must be defined (e.g., 'processes')."
            }

            if (ext.profiles == null || ext.profiles.isEmpty()) {
                errors << "ProcessPlugin: 'profiles' list must be defined and cannot be empty."
            }

            // Check SPI configuration
            if (!ext.spiInterface) {
                errors << "ProcessPlugin: 'spiInterface' must be defined (e.g., 'org.ceskaexpedice.processplatform.api.PluginSpi')."
            }
            if (!ext.spiImplementation) {
                errors << "ProcessPlugin: 'spiImplementation' must be defined (e.g., 'cz.incad.kramerius.plugin.MyProcessSPI')."
            }

            // If errors were found, halt the build
            if (!errors.isEmpty()) {
                throw new org.gradle.api.GradleException("\n" +
                        "--- Process Plugin Configuration Error ---\n" +
                        errors.join('\n') + "\n" +
                        "------------------------------------------"
                )
            }
	        def generateProfileTask = project.tasks.register("generateProcessPluginProfile") {
                group = "build"
                description = "Generates profile.json for process plugin"

//                def outputDir = new File(project.buildDir, "generated/resources")
//                def jsonFile = new File(outputDir, "profile.json")

                def outputDir = project.layout.buildDirectory.dir("generated/resources")
                def jsonFile = outputDir.map { it.file("profile.json") }

                outputs.file(jsonFile)

                doLast {
					outputDir.mkdirs()
					def jsonContent = JsonOutput.prettyPrint(
                            JsonOutput.toJson(ext.profiles)
					)
					jsonFile.text = jsonContent
					println "Generated profile.json at ${jsonFile}"

                }
            }

            def generateSpiTask = project.tasks.register("generateProcessPluginSpi") {
                group = "build"
                description = "Generates META-INF/services file for process plugin SPI"

//                def outputDir = new File(project.buildDir, "generated/resources/META-INF/services")
//                def spiFile = new File(outputDir, ext.spiInterface)

                def outputDir = project.layout.buildDirectory.dir("generated/resources/META-INF/services")
                def spiFile = outputDir.map { dir -> dir.file(ext.spiInterface) }


                onlyIf {
                    ext.spiInterface && ext.spiImplementation
                }

                outputs.file(spiFile)

                doLast {
                    outputDir.mkdirs()
                    spiFile.text = ext.spiImplementation.trim()
                    println "Generated SPI file at ${spiFile} with content: ${ext.spiImplementation}"
                }
            }


            project.sourceSets.main.resources.srcDir(
                    project.layout.buildDirectory.dir("generated/resources")
            )

            project.tasks.named("processResources").configure {
                dependsOn(generateProfileTask)
                dependsOn(generateSpiTask)
            }



            project.tasks.register("buildProcessPlugin") {
                group = "build"
                description = "Builds plugin JAR and distribution"

                dependsOn project.tasks.named("jar")
                dependsOn project.configurations.runtimeClasspath

                doLast {
                    def distDir = project.layout.buildDirectory.dir("distributions/${ext.pluginName}")
                    distDir.mkdirs()

                    def jar = project.tasks.named("jar").get().archiveFile.get().asFile
                    project.copy {
                        from jar
                        into distDir
                    }


                    // copy dependencies
					project.configurations.runtimeClasspath.files.each { file ->
						project.copy {
							from file
							into distDir
						}
					}

                    // write JSON
					/*
                    def jsonFile = new File(distDir, "${ext.pluginName}.json")
                    jsonFile.text = groovy.json.JsonOutput.prettyPrint(
                        groovy.json.JsonOutput.toJson(ext.profiles)
                    )*/

                    println "Built process plugin: ${distDir}"
                }
            }

            project.tasks.named("build").configure {
                dependsOn "buildProcessPlugin"
            }
        }
    }
}
