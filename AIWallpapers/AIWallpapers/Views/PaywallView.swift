import StoreKit
import SwiftUI

struct PaywallView: View {
    @EnvironmentObject private var premium: PremiumManager
    @Environment(\.dismiss) private var dismiss
    @State private var purchaseError: String?
    @State private var purchasing = false

    var body: some View {
        NavigationStack {
            VStack(spacing: 24) {
                Image(systemName: "crown.fill")
                    .font(.system(size: 56))
                    .foregroundStyle(.yellow)

                Text("Unlock 4K Ultra HD")
                    .font(.largeTitle.bold())

                VStack(alignment: .leading, spacing: 12) {
                    benefit("4k.tv", "All 500 wallpapers in 4K (2160×3840)")
                    benefit("sparkles", "Every new AI drop included")
                    benefit("infinity", "Unlimited downloads")
                }

                if premium.isPremium {
                    Label("You're Premium — 4K unlocked", systemImage: "checkmark.seal.fill")
                        .font(.headline)
                        .foregroundStyle(.green)
                } else if premium.isLoadingProducts {
                    ProgressView()
                } else if premium.products.isEmpty {
                    Text("Products unavailable. Check your connection or try again later.")
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                        .multilineTextAlignment(.center)
                } else {
                    productButtons
                }

                if let purchaseError {
                    Text(purchaseError)
                        .font(.caption)
                        .foregroundStyle(.red)
                        .multilineTextAlignment(.center)
                }

                Button("Restore Purchases") {
                    Task { await premium.restorePurchases() }
                }
                .font(.footnote)
            }
            .padding(24)
            .frame(maxHeight: .infinity, alignment: .top)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Close") { dismiss() }
                }
            }
        }
    }

    private var productButtons: some View {
        VStack(spacing: 12) {
            ForEach(premium.products, id: \.id) { product in
                Button {
                    Task {
                        purchasing = true
                        defer { purchasing = false }
                        do {
                            try await premium.purchase(product)
                            if premium.isPremium { dismiss() }
                        } catch {
                            purchaseError = error.localizedDescription
                        }
                    }
                } label: {
                    VStack(spacing: 2) {
                        Text(product.displayName).font(.headline)
                        Text(priceLabel(for: product)).font(.caption)
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 8)
                }
                .buttonStyle(.borderedProminent)
                .tint(.yellow)
                .foregroundStyle(.black)
                .disabled(purchasing)
            }
        }
    }

    private func priceLabel(for product: Product) -> String {
        if let period = product.subscription?.subscriptionPeriod {
            let unit: String = switch period.unit {
            case .day: "day"
            case .week: "week"
            case .month: "month"
            case .year: "year"
            @unknown default: "period"
            }
            return "\(product.displayPrice) / \(unit)"
        }
        return "\(product.displayPrice) one time"
    }

    private func benefit(_ icon: String, _ text: String) -> some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .frame(width: 28)
                .foregroundStyle(.yellow)
            Text(text)
        }
    }
}
