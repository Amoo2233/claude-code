import Foundation
import Photos
import UIKit

/// Downloads a wallpaper at the requested quality and writes the original
/// bytes to the photo library (data-based so 4K JPEGs are saved lossless).
@MainActor
final class WallpaperSaver: ObservableObject {
    enum State: Equatable {
        case idle
        case downloading
        case saving
        case saved
        case failed(String)
    }

    @Published private(set) var state: State = .idle

    func save(_ wallpaper: Wallpaper, quality: WallpaperQuality) async {
        state = .downloading
        do {
            let (data, response) = try await URLSession.shared
                .data(from: wallpaper.imageURL(quality: quality))
            guard let http = response as? HTTPURLResponse, http.statusCode == 200,
                  UIImage(data: data) != nil else {
                throw URLError(.badServerResponse)
            }

            let status = await PHPhotoLibrary.requestAuthorization(for: .addOnly)
            guard status == .authorized || status == .limited else {
                state = .failed("Allow photo access in Settings to save wallpapers.")
                return
            }

            state = .saving
            try await PHPhotoLibrary.shared().performChanges {
                let request = PHAssetCreationRequest.forAsset()
                request.addResource(with: .photo, data: data, options: nil)
            }
            state = .saved
        } catch {
            state = .failed("Download failed. Check your connection and try again.")
        }
    }

    func reset() {
        state = .idle
    }
}
