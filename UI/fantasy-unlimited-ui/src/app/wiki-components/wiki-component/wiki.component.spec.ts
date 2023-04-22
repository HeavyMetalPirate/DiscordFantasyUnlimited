import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WikiComponent } from './wiki.component';

describe('WikiComponentComponent', () => {
  let component: WikiComponent;
  let fixture: ComponentFixture<WikiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ WikiComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WikiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
