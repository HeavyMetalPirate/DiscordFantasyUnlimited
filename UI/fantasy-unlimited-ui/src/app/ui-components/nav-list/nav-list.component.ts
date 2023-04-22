import { CommonModule } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  ContentChildren,
  HostBinding,
  Input,
  QueryList,
  ViewEncapsulation,
} from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatListModule } from '@angular/material/list';

import { ExpandOnActiveLinkDirective } from './expand-on-active-link.directive';
import { NavListItemComponent } from './nav-list-item.component';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
  selector: 'nav-list',
  styleUrls: ['./nav-list.component.scss'],
  templateUrl: './nav-list.component.html',
  standalone: true,
  imports: [
    MatListModule,
    CommonModule,
    RouterModule,
    MatExpansionModule,
    NavListItemComponent,
    ExpandOnActiveLinkDirective,
  ],
})
export class NavListComponent {
  @ContentChildren(NavListItemComponent)
  navListItemComponents: QueryList<NavListItemComponent> | null = null;

  @Input() expandable = false;

  @Input() title = '';

  @HostBinding('class.nav-list--expandable')
  get expandableClass() {
    return this.expandable;
  }
}
