//
//  LocationManager.swift
//  SpeedCheck
//
//  Created by Dariusz Baranczuk on 03/01/2025.
//

import Foundation
import CoreLocation

final class LocationManager: NSObject, CLLocationManagerDelegate, ObservableObject {
    
    @Published var lastKnownLocation: CLLocation?
    @Published var maxSpeed = 0.0
    
    private var manager = CLLocationManager()
    
    override init() {
        super.init()
        manager.delegate = self
        manager.desiredAccuracy = kCLLocationAccuracyBest
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        lastKnownLocation = locations.first
        
        if let location = lastKnownLocation, location.speed > maxSpeed {
            maxSpeed = location.speed
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: any Error) {
        print("failed to get location \(error)")
    }
    
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        checkAuthorization()
    }
    
    func stopLocationUpdates() {
        manager.stopUpdatingLocation()
    }
    
    func checkAuthorization() {
        manager.startUpdatingLocation()
        
        switch manager.authorizationStatus {
        case .notDetermined:
            manager.requestWhenInUseAuthorization()
        case .restricted:
            print("Location is restricted")
        case .denied:
            print("Location is denied")
        case .authorizedAlways:
            print("Location is authorizedAlways")
        case .authorizedWhenInUse:
            lastKnownLocation = manager.location
        @unknown default:
            print("Location service disabled")
        }
    }
}
