rootProject.name = "pensjon-opptjening-gcp-maskinporten-client"
include("pensjon-opptjening-gcp-maskinporten-client")
include("pensjon-opptjening-gcp-maskinporten-client-api")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("kotlin", "2.1.21")
            version("java", "21")
        }
    }
}
