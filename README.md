# process-platform

/worker
|
| > plugins/import/ a.jar., b.jar, c.jar

processes/ build.gradle
apply 'application'

configurations {
process
}

dependencies {
process 'org.krameirus🅰️1.0'
process 'org.krameirus🅱️1.0'
process 'org.kramerius:c:1.0'
    api: 'process-platform:1.0'
}

task preparePlugins  {
// copy to plugins directory

}

configuration.properties
label=import

plugins {
id 'application'
id 'com.google.cloud.tools.jib' version '3.4.0'
}

application {
mainClass = 'your.main.Class' // nahraď skutečnou hlavní třídou
}

configurations {
process
}

dependencies {
process 'org.krameirus:import:1.0'
process 'org.krameirus:indexace:1.0'

    process 'org.krameirus:processlog:1.0'

    implementation 'process-platform:1.0'
}

// Kopírování pluginů do distribuce
task copyPlugins(type: Copy) {
description = 'Copies all process dependencies to the distribution plugins directory'
group = 'distribution'

    from configurations.process
    into "$buildDir/install/${project.name}/plugins"
}


// Vlastní task, který kombinuje installDist + copyPlugins
task prepareWorker {
description = 'Prepares full application distribution with plugins'
group = 'build'

    dependsOn 'installDist', 'copyPlugins'
}

//

generovatpdf(pro pavel.stastny) - -3

import(uuid:xxx) - spustil vasek jirousek
import(uuid:yyy) - spustila hrzinovana (jana)
import(uuid:ooo)
---------------

reindex(uuid:xxx) - import(uuid:xxx)
reindex(uuid:ppp)
reindex(uuid:zzz)


MAnager bude vedet pavel.stastny = a,b,c

worker_1

dej_mi_praci(import,indexace,processlog)

mam pro tebe praci import(uuid:xxx)
pracuju na uuid:xxxx,  musim reindexovat uuid:xxx,uuid:ppp,uuid:zzz

dej_mi_praci(import,indexace,processlog, plugins... )

dej_mi_praci(ai_models)

-----------------------------------------------------------------------------------------------
plugins {
id 'application'
id 'com.google.cloud.tools.jib' version '3.4.0'
}

application {
mainClass = 'your.main.Class' // nahraď skutečnou hlavní třídou
}

configurations {
process
}

dependencies {
process 'org.krameirus:a:1.0'
process 'org.krameirus:b:1.0'
process 'org.kramerius:c:1.0'

    implementation 'process-platform:1.0'
}

// Kopírování pluginů do distribuce
task copyPlugins(type: Copy) {
description = 'Copies all process dependencies to the distribution plugins directory'
group = 'distribution'

    from configurations.process
    into "$buildDir/install/${project.name}/plugins"
}

// Vlastní task, který kombinuje installDist + copyPlugins
task prepareWorker {
description = 'Prepares full application distribution with plugins'
group = 'build'

    dependsOn 'installDist', 'copyPlugins'
}

./gradlew prepareWorker → vytvoří celou distribuci včetně pluginů.

Pluginy skončí ve složce: build/install/<project>/plugins

Z toho pak můžeš stavět image (např. pomocí Jib nebo Dockerfile).
----------------------------------------------------------------------------------