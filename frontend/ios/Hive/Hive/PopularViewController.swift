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
    public private(set) var popularLocations: Array<Location> = []
    public var popularPosts: Array<Post> = []
    var client: ServerClient = ServerClient()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.hideKeyboardWhenTapped()
        setupPopularCollectionView()
        setupPostTableView()
        setupGestureToMainView()
        getPopularLocations()
    }
    
    private func setupGestureToMainView() {
        let gesture = UISwipeGestureRecognizer(target: self, action: #selector(gestureToMainCompletion))
        gesture.numberOfTouchesRequired = 1
        gesture.direction = .right
        postTableView.addGestureRecognizer(gesture)
    }
    
    @objc func gestureToMainCompletion(recognizer: UISwipeGestureRecognizer) {
        self.navigationController?.popViewController(animated: true)
        self.dismiss(animated: true, completion: nil)
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
    
    public func getPopularPostsFromLocation(location: Location) {
        client.getAllPopularPostsAtLocation(username: self.getTestUser(),
                                            latitude: location.latStr,
                                            longitude: location.lonStr,
                                            completion: getPopularPostsFromLocationCompletion)
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
            let userLocation = Location(locationStr: self.getTestLocation(), label: "My Area")
            popularLocations.append(userLocation)
            popularLocations.append(contentsOf: response.get().locations)
            
            print("BLAH BLAH")
            for location in popularLocations {
                print("BLAH " + location.label)
            }
            getPopularPostsFromLocation(location: userLocation)
            DispatchQueue.main.async {
                self.popularCollectionView.reloadData()
            }
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "seeCommentsSegue") {
            let postView: PostView = sender as! PostView
            let commentsViewController: CommentsViewController = segue.destination as! CommentsViewController
            commentsViewController.controllerInit(post: postView.post!)
            return
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
        cell.delegate = self
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
        cell.configure(location: popularLocations[indexPath.item], viewPosition: indexPath.item)
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
        self.popularPosts.removeAll()
        DispatchQueue.main.async {
            self.postTableView.reloadData()
        }
        self.getPopularPostsFromLocation(location: popularCollectionViewCell.location)
    }
}
