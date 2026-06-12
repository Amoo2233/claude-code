import SwiftUI

struct CategoriesView: View {
    @EnvironmentObject private var catalog: WallpaperCatalog

    var body: some View {
        NavigationStack {
            List(catalog.categories, id: \.self) { category in
                NavigationLink {
                    CategoryDetailView(category: category)
                } label: {
                    HStack(spacing: 12) {
                        if let cover = catalog.wallpapers(in: category).first {
                            AsyncImage(url: cover.thumbnailURL) { phase in
                                (phase.image ?? Image(systemName: "photo"))
                                    .resizable()
                                    .aspectRatio(contentMode: .fill)
                            }
                            .frame(width: 44, height: 60)
                            .clipShape(RoundedRectangle(cornerRadius: 8))
                        }
                        VStack(alignment: .leading) {
                            Text(category).font(.headline)
                            Text("\(catalog.wallpapers(in: category).count) wallpapers")
                                .font(.caption)
                                .foregroundStyle(.secondary)
                        }
                    }
                }
            }
            .navigationTitle("Categories")
        }
    }
}

struct CategoryDetailView: View {
    let category: String
    @EnvironmentObject private var catalog: WallpaperCatalog

    var body: some View {
        ScrollView {
            WallpaperGrid(wallpapers: catalog.wallpapers(in: category))
        }
        .navigationTitle(category)
        .navigationBarTitleDisplayMode(.inline)
    }
}
