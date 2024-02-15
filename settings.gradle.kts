rootProject.name = "pensjon-opptjening-gcp-maskinporten-client"
include("pensjon-opptjening-gcp-maskinporten-client")
include("pensjon-opptjening-gcp-maskinporten-client-api")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("kotlin", "1.9.22")
            version("java", "21")
        }
    }
}
