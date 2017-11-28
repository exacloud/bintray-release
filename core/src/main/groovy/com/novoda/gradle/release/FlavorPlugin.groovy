package com.novoda.gradle.release

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class FlavorPlugin implements Plugin<Project> {
    public static final FLAVOR = 'Flavor'
    public static CURRENT_FLAVOR

    void apply(Project project) {
        CURRENT_FLAVOR = checkCurrentFlavor(project)
    }

    def static checkCurrentFlavor(Project project) {
        def flavor = System.getenv(FLAVOR)
        if (flavor == null) {
            Properties properties = new Properties()

            File file = project.rootProject.file('flavor.properties');
            if (!file.exists()) {
                file.createNewFile()
                writeFlavorFile(file)
            }
            properties.load(new FileInputStream(file))
            flavor = properties.getProperty(FLAVOR)
        }
        if (flavor == null || flavor.length() == 0) {
            throw new GradleException('Please define nonempty FLAVOR in environment variables or root project\'s flavor.properties file!')
        }

        project.android.flavorDimensions("app")
        project.android.defaultPublishConfig(flavor + "Release")
        project.android.productFlavors.create(flavor, {dimension "app"})

        return flavor
    }

    def static writeFlavorFile(File file) {
        try {
            FileWriter writer = new FileWriter(file)
            writer.write("# You can use 'kujiale' or 'designer' to open different flavors. \n")
            writer.write(FLAVOR + "=")
            writer.close()
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

}
