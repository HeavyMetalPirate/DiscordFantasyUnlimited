import {Component, inject} from '@angular/core';
import { CommonModule } from '@angular/common';
import {ContentListingsService} from "../core/api/content";
import {shareReplay} from "rxjs";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {
  contentService = inject(ContentListingsService);
  title = this.contentService.getWeapons();
}
