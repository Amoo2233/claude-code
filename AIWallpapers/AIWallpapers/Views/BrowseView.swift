import SwiftUI

struct BrowseView: View {
    @EnvironmentObject private var catalog: WallpaperCatalog
    @EnvironmentObject private var premium: PremiumManager
    @State private var query = ""
    @State private var showPaywall = false

    var body: some View {
        NavigationStack {
            ScrollView {
                WallpaperGrid(wallpapers: catalog.search(query))
            }
            .navigationTitle("AI Wallpapers")
            .searchable(text: $query, prompt: "Search 500 wallpapers")
            .toolbar {
                if !premium.isPremium {
                    Button {
                        showPaywall = true
                    } label: {
                        Label("Go Premium", systemImage: "crown.fill")
                            .labelStyle(.titleAndIcon)
                            .font(.subheadline.bold())
                    }
                    .buttonStyle(.borderedProminent)
                    .tint(.yellow)
                    .foregroundStyle(.black)
                }
            }
            .sheet(isPresented: $showPaywall) { PaywallView() }
        }
    }
}

struct WallpaperGrid: View {
    let wallpapers: [Wallpaper]

    private let columns = [GridItem(.adaptive(minimum: 110), spacing: 8)]

    var body: some View {
        LazyVGrid(columns: columns, spacing: 8) {
            ForEach(wallpapers) { wallpaper in
                NavigationLink(value: wallpaper) {
                    WallpaperTile(wallpaper: wallpaper)
                }
                .buttonStyle(.plain)
            }
        }
        .padding(.horizontal, 8)
        .navigationDestination(for: Wallpaper.self) { wallpaper in
            WallpaperDetailView(wallpaper: wallpaper)
        }
    }
}

struct WallpaperTile: View {
    let wallpaper: Wallpaper
    @EnvironmentObject private var favorites: FavoritesStore

    var body: some View {
        AsyncImage(url: wallpaper.thumbnailURL) { phase in
            switch phase {
            case .success(let image):
                image.resizable().aspectRatio(contentMode: .fill)
            case .failure:
                Color.secondary.opacity(0.2)
                    .overlay(Image(systemName: "photo").foregroundStyle(.secondary))
            default:
                Color.secondary.opacity(0.1)
                    .overlay(ProgressView())
            }
        }
        .aspectRatio(9 / 16, contentMode: .fit)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(alignment: .topTrailing) {
            if favorites.contains(wallpaper) {
                Image(systemName: "heart.fill")
                    .font(.caption)
                    .foregroundStyle(.white, .red)
                    .padding(6)
                    .shadow(radius: 2)
            }
        }
        .overlay(alignment: .bottomLeading) {
            Text(wallpaper.title)
                .font(.caption2.weight(.medium))
                .lineLimit(1)
                .foregroundStyle(.white)
                .padding(.horizontal, 6)
                .padding(.vertical, 8)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(.black.opacity(0.35), in: UnevenRoundedRectangle(
                    bottomLeadingRadius: 12, bottomTrailingRadius: 12))
        }
    }
}
