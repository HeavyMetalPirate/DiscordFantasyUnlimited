import {Component, inject} from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslocoService } from "@ngneat/transloco";

@Component({
  selector: 'app-language-selector',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div>
      <button
        *ngFor="let language of languagesList; index as i"
        (click)="changeLanguage(language.code)"
      >
        <img [src]="language.imgUrl" [alt]="language.name" />
        <span> {{ language.shorthand }} </span>
      </button>
    </div>
  `
})
export class LanguageSelectorComponent {
  private transloco = inject(TranslocoService)

  public languagesList:
    Array<Record<'imgUrl' | 'code' | 'name' | 'shorthand', string>> = [
    {
      imgUrl: '/assets/images/English.png',
      code: 'en',
      name: 'English',
      shorthand: 'ENG',
    },
    {
      imgUrl: '/assets/images/Deutsch.png',
      code: 'de',
      name: 'German',
      shorthand: 'GER',
    },
  ];
  changeLanguage(languageCode: string) {
    this.transloco.setActiveLang(languageCode);
  }
}
