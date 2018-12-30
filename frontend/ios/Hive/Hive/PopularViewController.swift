//
//  PopularViewController.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 12/28/18.
//  Copyright © 2018 Chuck Onwuzuruike. All rights reserved.
//

import Foundation
import UIKit
import CoreLocation

class PopularViewController: UIViewController {
    
    @IBOutlet weak var popularCollectionView: UICollectionView!
    @IBOutlet weak var postTableView: UITableView!
    public private(set) var popularLocations: Array<String> = []
    public var popularPosts: Array<Post> = []
    var client: ServerClient = ServerClient()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.hideKeyboardWhenTapped()
        setupPopularCollectionView()
        setupPostTableView()
        getPopularLocations()
    }
    
    private func setupPostTableView() {
        postTableView.delegate = self
        postTableView.dataSource = self
        postTableView.register(UINib(nibName: "PostView", bundle: nil), forCellReuseIdentifier: "postViewCell")
    }
    
    private func setupPopularCollectionView() {
        popularCollectionView.delegate = self
        popularCollectionView.dataSource = self
    }
    
    public func getPopularPostsFromLocation(location: String) {
        let lat: String = location.split(separator: ":")[0].description
        let lon: String = location.split(separator: ":")[1].description
        client.getAllPopularPostsAtLocation(username: self.getTestUser(), latitude: lat, longitude: lon, completion: getPopularPostsFromLocationCompletion)
    }
    
    private func getPopularPostsFromLocationCompletion(response: StatusOr<Response>) {
        var error: Bool = false
        if (response.hasError()) {
            // Handle likley connection error
            print("Connection Failure: " + response.getErrorMessage())
            error = true
        }
        if (!error && response.get().serverStatusCode != ServerStatusCode.OK) {
            // Handle server error
            print("ServerStatusCode: " + String(describing: response.get().serverStatusCode))
            error = true
        }
        if (!error) {
            popularPosts.removeAll()
            popularPosts.append(contentsOf: response.get().posts)
            DispatchQueue.main.async {
                self.postTableView.reloadData()
            }
        }
    }
    
    private func getPopularLocations() {
        client.getPopularLocations(completion: getPopularLocationsCompletion)
    }
    
    private func getPopularLocationsCompletion(response: StatusOr<Response>) {
        var error: Bool = false
        if (response.hasError()) {
            // Handle likley connection error
            print("Connection Failure: " + response.getErrorMessage())
            error = true
        }
        if (!error && response.get().serverStatusCode != ServerStatusCode.OK) {
            // Handle server error
            print("ServerStatusCode: " + String(describing: response.get().serverStatusCode))
            error = true
        }
        if (!error) {
            popularLocations.removeAll()
            popularLocations.append("My Cluster")
            
            for location in response.get().locations {
                CLGeocoder().reverseGeocodeLocation(location) { (placemarks, error) in
                    if (error == nil) {
                        print(placemarks?[0])
                        let loc = (placemarks?[0].locality ?? "???") + ", "
                            + (placemarks?[0].administrativeArea ?? "???")
                        self.popularLocations.append(loc)
                        DispatchQueue.main.async {
                            self.popularCollectionView.reloadData()
                        }
                    } else {
                        print(error!)
                    }
                }
            }
        }
    }

}

//UITableViewExtensions
extension PopularViewController: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "postViewCell") as! PostView
        cell.configure(post: popularPosts[indexPath.section])
        cell.layer.borderWidth = 2
        cell.layer.cornerRadius = 5
        cell.layer.borderColor = UIColor.blue.cgColor
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 10
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 120
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return self.popularPosts.count
    }
}

// UIControllerView Extensions
extension PopularViewController: UICollectionViewDataSource, UICollectionViewDelegate {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return self.popularLocations.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "popularCollectionViewCell", for: indexPath as IndexPath) as! PopularCollectionViewCell
        cell.configure(label: popularLocations[indexPath.item], viewPosition: indexPath.item)
        cell.delegate = self
        return cell
    }
    
    
    func collectionView(_ collectionView: UICollectionView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
}

// PopularCollectionViewCell Extensions
extension PopularViewController: PopularCollectionViewCellDelegate {
    func popularButtonClicked(popularCollectionViewCell: PopularCollectionViewCell) {
        var location: String = ""
        if (popularCollectionViewCell.viewPosition == 0) {
            location = self.getTestLocation()
        } else {
            location = popularCollectionViewCell.label
        }
        self.popularPosts.removeAll()
        DispatchQueue.main.async {
            self.postTableView.reloadData()
        }
        self.getPopularPostsFromLocation(location: location)
    }
}
