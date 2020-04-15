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
    @IBOutlet weak var navBarTitle: UINavigationItem!
    
    public private(set) var popularLocations: Array<Location> = []
    private var client: ServerClient = ServerClient()
    private(set) var fetchPostsMetadata: QueryMetadata = QueryMetadata()
    private var postFeedManager = PostFeedManager()
    private var currLocation: Location?
    private var currentIndexPath: IndexPath = IndexPath(row: 0, section: 0)
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.postFeedTable.separatorColor = UIColor(red:0.97, green:0.82, blue:0.33, alpha:1.0)
        initialize()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        initialize()
    }
    
    private func initialize() {
        self.hideKeyboardWhenTapped()
        popularCollectionView.showsHorizontalScrollIndicator = false
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
    
    private func getPopularPostsFromLocationCompletion(responseOr: StatusOr<Response>, notes: [String:Any]?) {
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
            self.postFeedManager.reloadUI()
        }
    }
    
    private func getPopularLocations() {
        client.getPopularLocations(completion: getPopularLocationsCompletion, notes: nil)
    }
    
    private func getPopularLocationsCompletion(responseOr: StatusOr<Response>, notes: [String:Any]?) {
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
            popularLocations.append(contentsOf: response.locations)
            currLocation = popularLocations.first
            postFeedManager.pokeNew()
            DispatchQueue.main.async {
                self.popularCollectionView.reloadData()
            }
        }
    }
    
    @IBAction func leftArrowBnAction(_ sender: UIButton) {
        let item: Int = currentIndexPath.item
        if item - 1 > -1 {
            currentIndexPath.item = item - 1
        }
        DispatchQueue.main.async {
            self.popularCollectionView.scrollToItem(at: self.currentIndexPath, at: .centeredHorizontally, animated: true)
        }
    }
    
    @IBAction func rightArrowBnAction(_ sender: UIButton) {
        print(currentIndexPath)
        let item: Int = currentIndexPath.item
        if item + 1 < popularLocations.count {
            currentIndexPath.item = item + 1
        }
        print(currentIndexPath)
        DispatchQueue.main.async {
            self.popularCollectionView.scrollToItem(at: self.currentIndexPath, at: .centeredHorizontally, animated: true)
        }
    }
    
    public func refreshPosts(cell: PopularCollectionViewCell) {
        if (self.currLocation?.area == cell.location.area) {
            return
        }
        self.currLocation = cell.location
        postFeedManager.resetDataAndPokeNew()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "seeCommentsSegue") {
            let postView: PostView = sender as! PostView
            let commentsViewController: CommentsViewController = segue.destination as! CommentsViewController
            commentsViewController.controllerInit(post: postView.post,
                                                  disallowInteractingWithComments:  true)
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
            print("No set location.")
            return
        }
        client.getAllPopularPostsAtLocation(username: self.getLoggedInUsername(), queryParams: queryParams,
                                            location: self.currLocation!,
                                            completion: getPopularPostsFromLocationCompletion,
                                            notes: nil)
    }
    
    func performAction(post: Post, actionType: ActionType) {
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
        
        // Since one cell is visible at a time, this is where we refresh the posts.
        self.refreshPosts(cell: cell)
        return cell
    }
    
    
    func collectionView(_ collectionView: UICollectionView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
}
