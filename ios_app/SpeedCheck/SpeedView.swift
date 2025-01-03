//
//  SpeedView.swift
//  SpeedCheck
//
//  Created by Dariusz Baranczuk on 02/01/2025.
//

import SwiftUI

struct SpeedView: View {
    
    @StateObject private var locationManager = LocationManager()
    
    var body: some View {
        VStack {
            if let coordinate = locationManager.lastKnownLocation {
                
                HStack(alignment: .bottom) {
                    Text(String(format: "%.2f", coordinate.speed * 3.6))
                        .font(.system(size: 72))
                    Text("km\\h")
                        .font(.system(size: 24))
                        .padding(0)
                        .padding(.bottom, 12)
                        .foregroundColor(.black.opacity(0.7))
                }
                
                HStack(alignment: .bottom) {
                    Text("Max speed:")
                        .foregroundColor(.black.opacity(0.7))
                    Text(String(format: "%.2fkm\\h", locationManager.maxSpeed * 3.6))
                }
                
                HStack(alignment: .bottom) {
                    Text("Location:")
                        .foregroundColor(.black.opacity(0.7))
                    Text("\(String(format: "%.2f", coordinate.coordinate.latitude)), \(String(format: "%.2f", coordinate.coordinate.longitude))")
                }
                
                HStack(alignment: .bottom) {
                    Text("Altitude:")
                        .foregroundColor(.black.opacity(0.7))
                    Text("\(Int(coordinate.altitude))m")
                }
                
            } else {
                Text("Waiting...")
            }
        }
        .onAppear {
            locationManager.checkAuthorization()
        }
        .onDisappear {
            locationManager.stopLocationUpdates()
        }
    }
}

#Preview {
    SpeedView()
}
