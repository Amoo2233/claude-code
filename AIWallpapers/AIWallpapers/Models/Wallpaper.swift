import Foundation

struct Wallpaper: Identifiable, Codable, Hashable {
    let id: String
    let title: String
    let category: String
    let prompt: String

    var thumbnailURL: URL { CatalogConfig.imageURL(id: id, quality: .sd480) }

    func imageURL(quality: WallpaperQuality) -> URL {
        CatalogConfig.imageURL(id: id, quality: quality)
    }
}

struct WallpaperManifest: Codable {
    let wallpapers: [Wallpaper]
}

enum WallpaperQuality: String, CaseIterable {
    case sd480 = "480p"
    case uhd4k = "4k"

    var pixelSize: (width: Int, height: Int) {
        switch self {
        case .sd480: (480, 854)
        case .uhd4k: (2160, 3840)
        }
    }

    var displayName: String {
        switch self {
        case .sd480: "480p Standard"
        case .uhd4k: "4K Ultra HD"
        }
    }

    var badge: String {
        switch self {
        case .sd480: "480p"
        case .uhd4k: "4K"
        }
    }
}
