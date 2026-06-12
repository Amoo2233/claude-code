import SwiftUI

struct ContentView: View {
    @EnvironmentObject private var catalog: WallpaperCatalog

    var body: some View {
        TabView {
            BrowseView()
                .tabItem { Label("Browse", systemImage: "photo.on.rectangle.angled") }
            CategoriesView()
                .tabItem { Label("Categories", systemImage: "square.grid.2x2") }
            FavoritesView()
                .tabItem { Label("Favorites", systemImage: "heart") }
            SettingsView()
                .tabItem { Label("Settings", systemImage: "gearshape") }
        }
        .overlay {
            if let error = catalog.loadError {
                ContentUnavailableView(
                    "Catalog unavailable",
                    systemImage: "exclamationmark.triangle",
                    description: Text(error)
                )
            }
        }
    }
}

#Preview {
    ContentView()
        .environmentObject(WallpaperCatalog())
        .environmentObject(PremiumManager())
        .environmentObject(FavoritesStore())
}
