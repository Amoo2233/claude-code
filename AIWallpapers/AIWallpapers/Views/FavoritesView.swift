import SwiftUI

struct FavoritesView: View {
    @EnvironmentObject private var catalog: WallpaperCatalog
    @EnvironmentObject private var favorites: FavoritesStore

    private var favoriteWallpapers: [Wallpaper] {
        catalog.wallpapers.filter { favorites.ids.contains($0.id) }
    }

    var body: some View {
        NavigationStack {
            Group {
                if favoriteWallpapers.isEmpty {
                    ContentUnavailableView(
                        "No favorites yet",
                        systemImage: "heart",
                        description: Text("Tap the heart on any wallpaper to keep it here.")
                    )
                } else {
                    ScrollView {
                        WallpaperGrid(wallpapers: favoriteWallpapers)
                    }
                }
            }
            .navigationTitle("Favorites")
        }
    }
}
