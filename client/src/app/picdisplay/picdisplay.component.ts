import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { ImageService } from '../image.service';
import { Observable, Subscription } from 'rxjs';
import { ImageDataAsString } from '../model';

@Component({
  selector: 'app-picdisplay',
  templateUrl: './picdisplay.component.html',
  styleUrls: ['./picdisplay.component.css']
})
export class PicdisplayComponent implements OnInit, OnDestroy {

    key = 'e450b8e6'
    image!: string
    imageSub$!: Subscription

    imageService = inject(ImageService)

    ngOnInit(): void {
        this.imageSub$ = this.imageService.getImage(this.key).subscribe((data: ImageDataAsString) => {
            this.image = data.image
            console.info(data)
        })

        // this.imageService.getLikes(this.key).subscribe()
    }

    ngOnDestroy(): void {
        this.imageSub$.unsubscribe()
    }

    getImage(): string {
        return this.image
    }
}
