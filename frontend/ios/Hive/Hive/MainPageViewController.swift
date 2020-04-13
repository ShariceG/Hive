//
//  MainPageViewController.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 9/28/19.
//  Copyright © 2019 Chuck Onwuzuruike. All rights reserved.
//

import CoreLocation
import UIKit

class MainPageViewController : UIPageViewController {
    
    private var pages: Array<UIViewController> = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupLocationServices()
    }
    
    private func initPageViewController() {
        self.dataSource = self
        pages.append(newViewController(storyboardId: "MenuViewController"))
        pages.append(newViewController(storyboardId: "ViewController"))
        pages.append(newViewController(storyboardId: "PopularViewController"))
        self.setViewControllers([pages[1]], direction: .forward, animated: true, completion: nil)
    }
    
    private func setupLocationServices() {
        Global.environment?.initLocationHandler()
        Global.environment?.setLocationHandlerDelegate(delegate: self)
    }
    
    private func newViewController(storyboardId: String) -> UIViewController {
        return UIStoryboard(name: "Main", bundle: nil) .
            instantiateViewController(withIdentifier: storyboardId)
    }
    
    private func outOfBounds(i: Int, arr: Array<NSObject>) -> Bool {
        return !(i < arr.count && i > -1)
    }
    
}

// MARK: UIPageViewControllerDataSource
extension MainPageViewController: UIPageViewControllerDataSource {
 
    func pageViewController(_ pageViewController: UIPageViewController,
                            viewControllerAfter viewController: UIViewController) -> UIViewController? {
        let next = pages.firstIndex(of: viewController)! + 1
        if self.outOfBounds(i: next, arr: pages) {
            return nil
        }
        return pages[next]
    }
    
    func pageViewController(_ pageViewController: UIPageViewController,
                            viewControllerBefore viewController: UIViewController) -> UIViewController? {
        let next = pages.firstIndex(of: viewController)! - 1
        if self.outOfBounds(i: next, arr: pages) {
            return nil
        }
        return pages[next]
    }
}

extension MainPageViewController: LocationHandlerDelegate {
    func userDeniedLocationPermission() {
        DispatchQueue.main.async {
            let alert = UIAlertController(title: "Please enable location services", message: "", preferredStyle: .alert)
            let settingsAction = UIAlertAction(title: "Settings", style: .default, handler: { action in
                UIApplication.shared.open(URL(string: UIApplication.openSettingsURLString)!)
            })
            alert.addAction(settingsAction)
            alert.preferredAction = settingsAction
            self.presentAlert(alert: alert)
        }
    }
    
    func userApprovedLocationPermission() {
        if Global.environment!.locationHandler!.hasCurrentLocation() {
            return
        }
        showPermanentAlert(title: "Trying to Find Your Location", message: "If this takes a while, restart the app")
    }
    
    func locationUpdate(location: CLLocation) {
        dismissAlert()
        if pages.isEmpty {
            initPageViewController()
        }
    }
}
