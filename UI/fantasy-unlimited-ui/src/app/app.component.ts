import { Component } from '@angular/core';
import {inject} from "@angular/core";
import {ContentListingsService} from "./core/api/content";
import {AsyncPipe, JsonPipe} from "@angular/common";
import {SidebarComponent} from "./sidebar/sidebar.component";
import {MatSidenavModule} from "@angular/material/sidenav";
import {HomeComponent} from "./home/home.component";
import {BannerComponent} from "./banner/banner.component";
import {RouterOutlet} from "@angular/router";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  standalone: true,
  imports: [
    JsonPipe, AsyncPipe, SidebarComponent, MatSidenavModule, HomeComponent, BannerComponent, RouterOutlet
  ]
})
export class AppComponent {

}
