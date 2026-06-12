import SwiftUI

@main
struct AIWallpapersApp: App {
    @StateObject private var catalog = WallpaperCatalog()
    @StateObject private var premium = PremiumManager()
    @StateObject private var favorites = FavoritesStore()

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(catalog)
                .environmentObject(premium)
                .environmentObject(favorites)
        }
    }
}
