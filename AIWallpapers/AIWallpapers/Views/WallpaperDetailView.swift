import SwiftUI

struct WallpaperDetailView: View {
    let wallpaper: Wallpaper

    @EnvironmentObject private var premium: PremiumManager
    @EnvironmentObject private var favorites: FavoritesStore
    @StateObject private var saver = WallpaperSaver()
    @State private var showPaywall = false

    var body: some View {
        ZStack {
            Color.black.ignoresSafeArea()

            AsyncImage(url: wallpaper.imageURL(quality: premium.maxQuality)) { phase in
                switch phase {
                case .success(let image):
                    image.resizable().aspectRatio(contentMode: .fit)
                case .failure:
                    ContentUnavailableView(
                        "Couldn't load wallpaper",
                        systemImage: "wifi.slash",
                        description: Text("Check your connection and try again.")
                    )
                    .foregroundStyle(.white)
                default:
                    ProgressView().tint(.white)
                }
            }
        }
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .principal) {
                VStack {
                    Text(wallpaper.title).font(.headline)
                    Text(wallpaper.category).font(.caption).foregroundStyle(.secondary)
                }
            }
            ToolbarItem(placement: .topBarTrailing) {
                Button {
                    favorites.toggle(wallpaper)
                } label: {
                    Image(systemName: favorites.contains(wallpaper) ? "heart.fill" : "heart")
                        .foregroundStyle(favorites.contains(wallpaper) ? .red : .primary)
                }
            }
        }
        .safeAreaInset(edge: .bottom) { downloadBar }
        .sheet(isPresented: $showPaywall) { PaywallView() }
        .onChange(of: premium.isPremium) { saver.reset() }
    }

    private var downloadBar: some View {
        VStack(spacing: 10) {
            HStack {
                Label(premium.maxQuality.displayName,
                      systemImage: premium.isPremium ? "crown.fill" : "rectangle.compress.vertical")
                    .font(.caption.bold())
                    .padding(.horizontal, 10)
                    .padding(.vertical, 5)
                    .background(premium.isPremium ? .yellow : .gray.opacity(0.4),
                                in: Capsule())
                    .foregroundStyle(premium.isPremium ? .black : .white)
                Spacer()
                if !premium.isPremium {
                    Button("Unlock 4K") { showPaywall = true }
                        .font(.caption.bold())
                        .buttonStyle(.borderedProminent)
                        .tint(.yellow)
                        .foregroundStyle(.black)
                }
            }

            Button {
                Task { await saver.save(wallpaper, quality: premium.maxQuality) }
            } label: {
                Group {
                    switch saver.state {
                    case .idle:
                        Label("Download \(premium.maxQuality.badge)",
                              systemImage: "arrow.down.circle.fill")
                    case .downloading:
                        Label("Downloading…", systemImage: "arrow.down.circle")
                    case .saving:
                        Label("Saving…", systemImage: "photo.badge.arrow.down")
                    case .saved:
                        Label("Saved to Photos", systemImage: "checkmark.circle.fill")
                    case .failed:
                        Label("Try Again", systemImage: "exclamationmark.circle")
                    }
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 6)
            }
            .buttonStyle(.borderedProminent)
            .disabled(saver.state == .downloading || saver.state == .saving)

            if case .failed(let message) = saver.state {
                Text(message)
                    .font(.caption)
                    .foregroundStyle(.red)
                    .multilineTextAlignment(.center)
            }
        }
        .padding()
        .background(.ultraThinMaterial)
    }
}
