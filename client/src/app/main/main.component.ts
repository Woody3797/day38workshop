import { AfterViewInit, Component, EventEmitter, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { WebcamImage } from 'ngx-webcam';
import { Observable, Subject, tap } from 'rxjs';
import { ImageService } from '../image.service';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {

    trigger$!: Subject<void>
    image!: WebcamImage
    mirrorImage!: string
    height!: number
    width!: number

    router = inject(Router)
    imageService = inject(ImageService)

    ngOnInit(): void {
        this.trigger$ = new Subject<void>
        this.mirrorImage = 'never'
        this.height = 400
        this.width = 400
    }

    captureImage(image: WebcamImage) {
        this.image = image
        this.imageService.image = image.imageAsDataUrl
        this.imageService.contentType = image['_mimeType']
    }

    takeSnap() {
        this.trigger$.next()
        this.router.navigate([ '/upload'])
    }
}
