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
manager bude mit prehled o vsem..  Takze bude schopen ridit workery, poda vsechny informace bez ohledu na to, kde se to spoustelo..
worker - nekde zmizel- musim import preplanovat...



worker bude - nemusel mit pristup do db
to api bude primarne urceno pro manager..,
bude poskytovat data o fyzicky bezicich proceses (ne z db),   java -cp  .....,
bude poskytovat informace  vytizeni (casem) a bude mit endpoint ~/health (zjisteni zdravi),,
bude mit endpoint pro logy

jeste dalsi pozmnaka.. debug bude mozny pres zverejneni portu ven.  (Docker compose) jenom by me zajimalo, zda umoznuje zverejnit port i kdyz jeste v kontejneru otevreny...
ale to by asi mel, to by nefungovalo spoustu veci
-------------------------------------------------------------------------------------
Contejner c.1

    /import.json 
      [
      {
        "type":"import-cgi"
        "mainClass":"Import",
        "jvmparams":xxxxxx
      },
      {
        "type":"import-cgi-nonsense"
        "mainClass":"Import",
        "jvmparams":xxxxxx
      }

      ]


    Runner 

/-----
plugins
import-- default.json //classpath{"type":"import","mainClass":"Import"},
index-- default.json,
neco -- default.json,
,


setlicense - plugin a on umi dva TYPY procesu
-- default.json -- bude obsahovat dve definice-- prvni s konstatnim parametrem ADD - typ procesu se jmenuje add-license-- druha s konstatnim parametrem REMOVE  - typ procesu se jmenuje remove-license,

                {
                  [
                    {
                        "type":"add-license",
                        "mainClass":"SetLicense",
                        "params":["ADD"]
                    },
                    {
                        "type":"remove-license",
                        "mainClass":"SetLicense",
                        "params":["REMOVE"]
                    },
                    {
                        "type":"add-license-jvmparams",
                        "mainClass":"SetLicense"
                        "jvmparams":"-Duser.home=neco"

                    }
                  ]


pluginbezdefinice
-- runner mu priplacne default.json kde typ == pluginbezdefinice

,

        /next?types=import,neco,add-license,remove-license,pluginbezdefinice
        /plan?type=import
        ---------------------------------------------------------------------

        /plan?type=import_cgi
        /plan?type=add-license_cgi


        /next?types=import_cgi



        Contjner c.2 - musi byt v konfiguraci
        Runner
/-----
plugins
import-- default.json,
,









Runner probehne adresar plugins a snazi se najit vsechny pluginy/procesy,
Po startu da vedet process-manageru jake pluginy ma k dispozici2.1a - posle pouze typ procesu, tedy import, index, neco 2.1b - posle celou definici - default definici.  Kde bude jvmparametrs a konstatni parametry,
Manager si zaregistruje typ (pokud ho nema) a muzeme planovat. 3.1a  - zaregistruje si jenom typ3.1b - zaregistruje si celou default definici. - Umozni celou definici predelat,
,



Martin Duda a chce vlastni proces, ktery si bude spoustet sam ale ma difinici typu import
4.
