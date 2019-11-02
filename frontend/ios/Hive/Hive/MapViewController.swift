//
//  MapViewController.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 9/22/19.
//  Copyright © 2019 Chuck Onwuzuruike. All rights reserved.
//

import Foundation
import UIKit
import MapKit

class CustomMKPointAnnotation: MKPointAnnotation {
    public var location: Location?
}

class MapViewController: UIViewController, MKMapViewDelegate {
    
    let POST_FEED_MODAL_VIEW_SEGUE_IDENTIFIER = "seePostFeedModalViewSegue"
    
    @IBOutlet weak var mapView: MKMapView!
    private(set) var client: ServerClient = ServerClient()
    
    override func viewDidLoad() {
        mapView.delegate = self
        getLocations()
    }
    
    private func addMapAnnotation(location: Location) {
        let annotation = CustomMKPointAnnotation()
        annotation.location = location
        annotation.coordinate = location.location.coordinate
        annotation.title = location.area.city
        mapView.addAnnotation(annotation)
    }
    
    private func getLocations() {
        client.getAllPostLocations(completion: getAllPostLocationsCompletion)
    }
    
    private func getAllPostLocationsCompletion(responseOr: StatusOr<Response>) {
        var error: Bool = false
        if (responseOr.hasError()) {
            // Handle likley connection error
            print("Connection Failure: " + responseOr.getErrorMessage())
            error = true
        }
        let response = responseOr.get()
        if (!error && response.serverStatusCode != ServerStatusCode.OK) {
            // Handle server error
            print("ServerStatusCode: " + String(describing: response.serverStatusCode))
            error = true
        }
        if (error) {
            // Report error to user somehow
            return
        }
        
        DispatchQueue.main.async {
            for location in response.locations {
                self.addMapAnnotation(location: location)
            }
        }
    }
    
    func mapView(_ mapView: MKMapView, didSelect view: MKAnnotationView) {
        self.performSegue(withIdentifier: POST_FEED_MODAL_VIEW_SEGUE_IDENTIFIER, sender: view)
    }
    
    @IBAction func backToMenuAction(_ sender: UIButton) {
        _ = self.navigationController?.popViewController(animated: true)
        self.dismiss(animated: true, completion: nil)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == POST_FEED_MODAL_VIEW_SEGUE_IDENTIFIER) {
            let annotationView = sender as! MKAnnotationView
            let postFeedModalView = segue.destination as! PostFeedModalViewController
            let annotation = annotationView.annotation as! CustomMKPointAnnotation
            postFeedModalView.controllerInit(location: annotation.location!)
            return
        }
    }
    
}
