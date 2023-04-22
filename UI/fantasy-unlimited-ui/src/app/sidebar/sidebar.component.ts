import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MatSidenavModule} from "@angular/material/sidenav";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, MatSidenavModule, RouterLink],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {

}
