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
    
    let COMMENTS_SEGUE_IDENTIFIER = "seeCommentsSegue"
    
    @IBOutlet weak var popularCollectionView: UICollectionView!
    @IBOutlet weak var postFeedTable: UITableView!
    
    public private(set) var popularLocations: Array<Location> = []
    private var client: ServerClient = ServerClient()
    private(set) var fetchPostsMetadata: QueryMetadata = QueryMetadata()
    private var postFeedManager = PostFeedManager()
    private var currLocation: Location?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.hideKeyboardWhenTapped()
        setupPopularCollectionView()
        setupSwipeGestures()
        postFeedManager.configure(tableView: postFeedTable, delegate: self)
        getPopularLocations()
    }
    
    private func setupSwipeGestures() {
        let swipeDown = UISwipeGestureRecognizer(
            target: self, action: #selector(swipeGestureAction))
        swipeDown.direction = .down
        
        self.view?.addGestureRecognizer(swipeDown)
    }
    
    @objc func swipeGestureAction(recognizer: UISwipeGestureRecognizer) {
        switch recognizer.direction {
        case UISwipeGestureRecognizer.Direction.down:
            _ = self.navigationController?.popViewController(animated: true)
            self.dismiss(animated: true, completion: nil)
        default:
            break
        }
    }
    
    private func setupPopularCollectionView() {
        popularCollectionView.delegate = self
        popularCollectionView.dataSource = self
    }
    
    private func getPopularPostsFromLocationCompletion(responseOr: StatusOr<Response>) {
        if (responseOr.hasError()) {
            // Handle likley connection error
            print("Connection Failure: " + responseOr.getErrorMessage())
            return
        }
        if (!responseOr.get().ok()) {
            // Handle server error
            print("ServerStatusCode: " + String(describing: responseOr.get().serverStatusCode))
            return
        }
        
        postFeedManager.addMorePosts(morePosts: responseOr.get().posts,
                                     newMetadata: responseOr.get().queryMetadata)
        DispatchQueue.main.async {
            self.postFeedManager.reload()
        }
    }
    
    private func getPopularLocations() {
        client.getPopularLocations(completion: getPopularLocationsCompletion)
    }
    
    private func getPopularLocationsCompletion(responseOr: StatusOr<Response>) {
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
        if (!error) {
            popularLocations.removeAll()
            let userLocation = Location(locationStr: self.getTestLocation(), label: "My Area")
            popularLocations.append(userLocation)
            popularLocations.append(contentsOf: response.locations)
            currLocation = userLocation
            postFeedManager.pokeNew()
            DispatchQueue.main.async {
                self.popularCollectionView.reloadData()
            }
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "seeCommentsSegue") {
            let postView: PostView = sender as! PostView
            let commentsViewController: CommentsViewController = segue.destination as! CommentsViewController
            commentsViewController.controllerInit(post: postView.post)
            return
        }
    }

}

// Delegate functions
extension PopularViewController: PostFeedDelegate {
    func showComments(postView: PostView) {
        self.performSegue(withIdentifier: COMMENTS_SEGUE_IDENTIFIER, sender: postView)
    }
    
    func fetchMorePosts(queryParams: QueryParams) {
        if (currLocation == nil) {
            print("No set location. This is a bug. Potentially unrecoverable.")
            return
        }
        client.getAllPopularPostsAtLocation(username: self.getTestUser(), queryParams: queryParams,
                                            locationStr: self.currLocation!.locationStr,
                                            completion: getPopularPostsFromLocationCompletion)
    }
    
    func likePost(post: Post) {
    }
    
    func dislikePost(post: Post) {
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
        if (self.currLocation?.locationStr == popularCollectionViewCell.location.locationStr) {
            return
        }
        self.currLocation = popularCollectionViewCell.location
        postFeedManager.resetDataAndPokeNew()
    }
}
