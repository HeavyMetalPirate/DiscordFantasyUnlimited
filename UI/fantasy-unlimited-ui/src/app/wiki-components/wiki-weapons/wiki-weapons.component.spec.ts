import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WikiWeaponsComponent } from './wiki-weapons.component';

describe('WikiWeaponsComponent', () => {
  let component: WikiWeaponsComponent;
  let fixture: ComponentFixture<WikiWeaponsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ WikiWeaponsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WikiWeaponsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
