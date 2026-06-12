import SwiftUI

struct SettingsView: View {
    @EnvironmentObject private var premium: PremiumManager
    @EnvironmentObject private var catalog: WallpaperCatalog
    @State private var showPaywall = false

    var body: some View {
        NavigationStack {
            List {
                Section("Membership") {
                    HStack {
                        Label("Plan", systemImage: premium.isPremium ? "crown.fill" : "person")
                        Spacer()
                        Text(premium.isPremium ? "Premium" : "Free")
                            .foregroundStyle(premium.isPremium ? .yellow : .secondary)
                            .bold(premium.isPremium)
                    }
                    HStack {
                        Label("Download quality", systemImage: "arrow.down.circle")
                        Spacer()
                        Text(premium.maxQuality.displayName)
                            .foregroundStyle(.secondary)
                    }
                    if !premium.isPremium {
                        Button {
                            showPaywall = true
                        } label: {
                            Label("Upgrade to 4K Premium", systemImage: "sparkles")
                        }
                    }
                    Button("Restore Purchases") {
                        Task { await premium.restorePurchases() }
                    }
                }

                Section("Catalog") {
                    LabeledContent("Wallpapers", value: "\(catalog.wallpapers.count)")
                    LabeledContent("Categories", value: "\(catalog.categories.count)")
                    LabeledContent("Source", value: "AI generated")
                }

                Section {
                    LabeledContent("Version",
                                   value: Bundle.main.infoDictionary?["CFBundleShortVersionString"]
                                       as? String ?? "1.0")
                } footer: {
                    Text("All wallpapers are generated with AI. Premium unlocks 4K Ultra HD downloads; the free plan includes every wallpaper in 480p.")
                }
            }
            .navigationTitle("Settings")
            .sheet(isPresented: $showPaywall) { PaywallView() }
        }
    }
}
