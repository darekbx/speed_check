//
//  ContentView.swift
//  SpeedCheck
//
//  Created by Dariusz Baranczuk on 01/01/2025.
//

import SwiftUI
import CoreLocation

struct ContentView: View {
    @StateObject private var locationManager = LocationManager()
    
    var body: some View {
        let manager = CLLocationManager()
        let authorizationDenied = manager.authorizationStatus == .denied
        let locationEnabled = CLLocationManager.locationServicesEnabled()
        NavigationStack {
            VStack(alignment: .center) {
                Spacer()
                NavigationLink {
                    SpeedView()
                } label: {
                    Text("Start")
                        .font(.largeTitle)
                    
                }
                .disabled(authorizationDenied || !locationEnabled)
                Spacer()
                HStack {
                    if !authorizationDenied {
                        Image(systemName: "checkmark")
                            .foregroundColor(.green)
                    } else {
                        Image(systemName: "xmark")
                            .foregroundColor(.red)
                    }
                    Text("Location permissions")
                }
                
                HStack {
                    if locationEnabled {
                        Image(systemName: "checkmark")
                            .foregroundColor(.green)
                    } else {
                        Image(systemName: "xmark")
                            .foregroundColor(.red)
                    }
                    Text("Location is enabled")
                }
                .padding(.top, 1)
            }
            .padding()
            .onAppear {
                locationManager.checkAuthorization()
            }
        }
    }
}

#Preview {
    ContentView()
}
