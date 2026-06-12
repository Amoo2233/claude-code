import Foundation

@MainActor
final class WallpaperCatalog: ObservableObject {
    @Published private(set) var wallpapers: [Wallpaper] = []
    @Published private(set) var loadError: String?

    var categories: [String] {
        var seen = Set<String>()
        return wallpapers.compactMap { seen.insert($0.category).inserted ? $0.category : nil }
    }

    init() {
        load()
    }

    func wallpapers(in category: String) -> [Wallpaper] {
        wallpapers.filter { $0.category == category }
    }

    func search(_ query: String) -> [Wallpaper] {
        let trimmed = query.trimmingCharacters(in: .whitespaces)
        guard !trimmed.isEmpty else { return wallpapers }
        return wallpapers.filter {
            $0.title.localizedCaseInsensitiveContains(trimmed)
                || $0.category.localizedCaseInsensitiveContains(trimmed)
        }
    }

    private func load() {
        guard let url = Bundle.main.url(forResource: "wallpapers", withExtension: "json") else {
            loadError = "wallpapers.json missing from bundle"
            return
        }
        do {
            let data = try Data(contentsOf: url)
            wallpapers = try JSONDecoder().decode(WallpaperManifest.self, from: data).wallpapers
        } catch {
            loadError = "Failed to load catalog: \(error.localizedDescription)"
        }
    }
}
