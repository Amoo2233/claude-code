import Foundation

@MainActor
final class FavoritesStore: ObservableObject {
    @Published private(set) var ids: Set<String>

    private static let key = "favoriteWallpaperIDs"

    init() {
        ids = Set(UserDefaults.standard.stringArray(forKey: Self.key) ?? [])
    }

    func contains(_ wallpaper: Wallpaper) -> Bool {
        ids.contains(wallpaper.id)
    }

    func toggle(_ wallpaper: Wallpaper) {
        if !ids.insert(wallpaper.id).inserted {
            ids.remove(wallpaper.id)
        }
        UserDefaults.standard.set(Array(ids).sorted(), forKey: Self.key)
    }
}
