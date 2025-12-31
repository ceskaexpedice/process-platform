package org.ceskaexpedice.processplatform

import groovy.json.JsonOutput
import org.gradle.api.Plugin
import org.gradle.api.Project

class ProcessPluginExtension {
    String pluginName
    List<Map<String, Object>> profiles = []


    //String spiInterface
    String spiImplementation

    List<String> excludeDependencies = []

}

class ProcessPlugin implements Plugin<Project> {

    private static final String PROCESS_PLUGIN_SPI = "org.ceskaexpedice.processplatform.api.PluginSpi"

    void apply(Project project) {
        def ext = project.extensions.create("processPlugin", ProcessPluginExtension)



        project.tasks.named("jar") {
            manifest {
                attributes (
                        'Implementation-Title': project.name,
                        'Implementation-Version': project.version,

                        'Module-Group-Id': project.group,
                        'Module-Artifact-Id': project.name,
                        'Module-description': project.description
                )
            }
        }


        project.afterEvaluate {

            def errors = []

            if (!ext.spiImplementation) {
                errors << "ProcessPlugin: 'spiImplementation' must be defined (e.g., 'cz.incad.kramerius.plugin.MyProcessSPI')."
            }

            if (!errors.isEmpty()) {
                throw new org.gradle.api.GradleException("\n" +
                        "--- Process Plugin Configuration Error ---\n" +
                        errors.join('\n') + "\n" +
                        "------------------------------------------"
                )
            }

            if (ext.profiles.isEmpty()) {
                println "ProcessPlugin: 'profiles' not defined. Using project-specific default profile."

                def defaultProfile =                     [
                        "profileId": "${project.name}",
                        "description": "${project.description}",
                        jvmArgs: ["-Xms1g","-Xmx32g"]
                ];

                ext.profiles.add(defaultProfile)
            }

            if (!ext.pluginName) {
                ext.pluginName = "${project.name}"
            }

            def generateProfileTask = project.tasks.register("generateProcessPluginProfile") {
                group = "build"
                description = "Generates profile.json for process plugin"


                def outputDir = project.layout.buildDirectory.dir("generated/resources")
                def jsonFile = outputDir.map { it.file("profile.json") }

                outputs.file(jsonFile)

                doLast {

                    def outDirFile = outputDir.get().asFile
                    outDirFile.mkdirs()

                    def jsonOutFile = jsonFile.get().asFile
                    def jsonContent = JsonOutput.prettyPrint(
                            JsonOutput.toJson(ext.profiles)
					)
                    jsonOutFile.text = jsonContent
					println "Generated profile.json at ${jsonOutFile}"

                }
            }

            def generateSpiTask = project.tasks.register("generateProcessPluginSpi") {
                group = "build"
                description = "Generates META-INF/services file for process plugin SPI"


                def outputDir = project.layout.buildDirectory.dir("generated/resources/META-INF/services")
                def spiFile = outputDir.map { dir -> dir.file(PROCESS_PLUGIN_SPI) }


                onlyIf {
                    ext.spiImplementation
                }

                outputs.file(spiFile)

                doLast {
                    def outDirFile = outputDir.get().asFile
                    outDirFile.mkdirs()

                    def spiOutFile = spiFile.get().asFile
                    spiOutFile.text = ext.spiImplementation.trim()

                    println "Generated SPI file at ${spiOutFile} with content: ${ext.spiImplementation}"
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

                    def distDirProvider = project.layout.buildDirectory.dir("distributions/${ext.pluginName}")
                    def distDir = distDirProvider.get().asFile
                    distDir.mkdirs()


                    def jar = project.tasks.named("jar").get().archiveFile.get().asFile
                    project.copy {
                        from jar
                        into distDir
                    }


                    // copy dependencies
					//project.configurations.runtimeClasspath.files.each { file ->
                    project.configurations.runtimeClasspath.resolvedConfiguration.resolvedArtifacts.each { artifact ->

                        def id = artifact.moduleVersion.id
                        def groupName = id.group
                        def artifactName = id.name
                        def fullName = "${groupName}:${artifactName}"

                        boolean isExcluded = ext.excludeDependencies.any { ex ->
                            groupName.startsWith(ex) || fullName.contains(ex)
                        }


                        if (!isExcluded) {
                            project.copy {
                                from artifact.file
                                into distDir
                            }
                        } else {
                            println "ProcessPlugin: Excluding ${fullName} from distribution."
                        }

					}
                    println "Built process plugin: ${distDir}"
                }
            }

            project.tasks.named("build").configure {
                dependsOn "buildProcessPlugin"
            }
        }
    }
}
