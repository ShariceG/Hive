//
//  MainTabBarViewController.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 4/17/20.
//  Copyright © 2020 Chuck Onwuzuruike. All rights reserved.
//

import CoreLocation
import UIKit

class MainTabBarController : UITabBarController {
    private var controllers: Array<UIViewController> = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupLocationServices()
    }
        
    private func initTabBarController() {
        let settings = newViewController(storyboardName: "Main", storyboardId: "MenuViewController")
        settings.tabBarItem = UITabBarItem()
        settings.tabBarItem.title = "Settings"
        
        let map = newViewController(storyboardName: "Main", storyboardId: "MapViewController")
        map.tabBarItem = UITabBarItem()
        map.tabBarItem.title = "Map"
        
        let main = newViewController(storyboardName: "Main", storyboardId: "ViewController")
        main.tabBarItem = UITabBarItem()
        main.tabBarItem.title = "Hive"
        
        let popular = newViewController(storyboardName: "Main", storyboardId: "PopularViewController")
        popular.tabBarItem = UITabBarItem()
        popular.tabBarItem.title = "Popular"
        
        controllers = [map, main, popular, settings]
        
        setViewControllers(controllers, animated: true)
        selectedViewController = main
    }
    
    private func setupLocationServices() {
        Global.environment?.initLocationHandler()
        Global.environment?.setLocationHandlerDelegate(delegate: self)
    }
    
    private func newViewController(storyboardId: String) -> UIViewController {
        return UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: storyboardId)
    }
}

extension MainTabBarController: LocationHandlerDelegate {
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
        if controllers.isEmpty {
            initTabBarController()
        }
    }
}
