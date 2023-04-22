import {bootstrapApplication} from "@angular/platform-browser";
import {AppComponent} from "./app/app.component";
import {provideHttpClient} from "@angular/common/http";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { importProvidersFrom } from '@angular/core';
import {provideRouter} from "@angular/router";
import {routes} from "./app/app.routes";

bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(),
    importProvidersFrom(BrowserAnimationsModule),
    provideRouter(routes)
  ]
});
