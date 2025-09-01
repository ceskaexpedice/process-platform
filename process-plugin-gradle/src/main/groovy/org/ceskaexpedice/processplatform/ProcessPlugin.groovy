package org.ceskaexpedice.processplatform

import org.gradle.api.Plugin
import org.gradle.api.Project

class ProcessPluginExtension {
    String pluginName
    List<Map<String, Object>> profiles = []
}

class ProcessPlugin implements Plugin<Project> {
    void apply(Project project) {
        def ext = project.extensions.create("processPlugin", ProcessPluginExtension)

        project.afterEvaluate {
		
	        def generateProfileTask = project.tasks.register("generateProcessPluginProfile") {
                group = "build"
                description = "Generates profile.json for process plugin"

                def outputDir = new File(project.buildDir, "generated/resources")
                def jsonFile = new File(outputDir, "profile.json")

                outputs.file(jsonFile)

                doLast {
                    //outputDir.mkdirs()
					//jsonFile.text = JsonOutput.prettyPrint(JsonOutput.toJson(ext.profiles))
                    //println "Generated profile.json at ${jsonFile}"
					
					outputDir.mkdirs()
					def jsonContent = groovy.json.JsonOutput.prettyPrint(
						groovy.json.JsonOutput.toJson(ext.profiles)
					)
					jsonFile.text = jsonContent
					println "Generated profile.json at ${jsonFile}"
					
                }
            }

            project.sourceSets.main.resources.srcDir("${project.buildDir}/generated/resources")
            project.tasks.named("processResources").configure {
                dependsOn(generateProfileTask)
            }

		
            project.tasks.register("buildProcessPlugin") {
                group = "build"
                description = "Builds plugin JAR and distribution"

                dependsOn project.tasks.named("jar")
                dependsOn project.configurations.runtimeClasspath

                doLast {
                    def distDir = new File(project.buildDir, "distributions/${ext.pluginName}")
                    distDir.mkdirs()

                    // copy plugin jar
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
