kover {
    reports {
        filters {
            excludes {
                classes(
                    "com.kamelia.sprinkler.util.VarargCopyWorkaround",
                    "com.kamelia.sprinkler.util.UnmodifiableCollectionsKt",
                )
            }
        }
    }
}

tasks {
    javadoc {
        exclude("**/com/kamelia/sprinkler/util/VarargCopyWorkaround.java")
    }
}
