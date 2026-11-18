rootProject.name = "pensjon-opptjening-gcp-maskinporten-client"
include("pensjon-opptjening-gcp-maskinporten-client")
include("pensjon-opptjening-gcp-maskinporten-client-api")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("kotlin", "2.4.21")
            version("java", "21")
            version("benManesVersions", "0.67.0")
            version("jacksonVersion", "2.23.0")
        }
    }
}
