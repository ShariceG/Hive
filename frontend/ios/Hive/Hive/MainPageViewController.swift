//
//  MainPageViewController.swift
//  Hive
//
//  Created by Chuck Onwuzuruike on 9/28/19.
//  Copyright © 2019 Chuck Onwuzuruike. All rights reserved.
//

import Foundation
import UIKit

class MainPageViewController : UIPageViewController {
    
    private var pages: Array<UIViewController> = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.dataSource = self
        pages.append(newViewController(storyboardId: "MenuViewController"))
        pages.append(newViewController(storyboardId: "ViewController"))
        pages.append(newViewController(storyboardId: "PopularViewController"))
        self.setViewControllers([pages[1]], direction: .forward, animated: true, completion: nil)
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
