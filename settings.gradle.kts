rootProject.name = "pensjon-opptjening-gcp-maskinporten-client"
include("pensjon-opptjening-gcp-maskinporten-client")
include("pensjon-opptjening-gcp-maskinporten-client-api")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("kotlin", "2.4.20")
            version("java", "21")
            version("benManesVersions", "0.54.0")
            version("jacksonVersion", "2.21.3")
        }
    }
}
