import Foundation

enum CatalogConfig {
    /// Public base URL of the generated wallpapers, laid out as
    /// `<baseURL>/4k/<id>.jpg` and `<baseURL>/480p/<id>.jpg`
    /// (the structure produced by tools/generate_wallpapers.py).
    static let baseURL = URL(string: "https://cdn.example.com/wallpapers")!

    /// While true the app serves deterministic placeholder images so it is
    /// fully browsable before the AI generation pipeline has been run and
    /// uploaded. Set to false once `baseURL` points at the real images.
    static let demoMode = true

    static func imageURL(id: String, quality: WallpaperQuality) -> URL {
        if demoMode {
            let size = quality.pixelSize
            return URL(string: "https://picsum.photos/seed/\(id)/\(size.width)/\(size.height)")!
        }
        return baseURL
            .appendingPathComponent(quality.rawValue)
            .appendingPathComponent("\(id).jpg")
    }
}
