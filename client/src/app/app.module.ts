import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule, Routes } from '@angular/router';
import {WebcamModule} from 'ngx-webcam';
import { MainComponent } from './main/main.component';
import { ImageComponent } from './image/image.component';
import { ImageService } from './image.service';
import { PicdisplayComponent } from './picdisplay/picdisplay.component';

const appRoutes: Routes = [
    { path: '', component: MainComponent, title: 'Webcam' },
    { path: 'upload', component: ImageComponent, title: 'Upload' },
    { path: 'picdisplay', component: PicdisplayComponent, title: 'Pics' },
    { path: '**', redirectTo: '/', pathMatch: 'full' },
]

@NgModule({
  declarations: [
    AppComponent,
    MainComponent,
    ImageComponent,
    PicdisplayComponent
  ],
  imports: [
    BrowserModule,
    ReactiveFormsModule,
    HttpClientModule,
    RouterModule.forRoot(appRoutes, { useHash: false }),
    WebcamModule,
  ],
  providers: [ ImageService ],
  bootstrap: [AppComponent]
})
export class AppModule { }
