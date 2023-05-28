import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { ImageService } from '../image.service';
import { Observable, Subscription } from 'rxjs';
import { ImageDataAsString, Post, S3FilesList } from '../model';

@Component({
  selector: 'app-picdisplay',
  templateUrl: './picdisplay.component.html',
  styleUrls: ['./picdisplay.component.css']
})
export class PicdisplayComponent implements OnInit, OnDestroy {

    key!: string
    image!: string
    comments!: string
    likes!: number
    dislikes!: number
    files!: string[]

    imageSub$!: Subscription
    postSub$!: Subscription
    filesSub$!: Subscription
    post$!: Observable<Post>
    files$!: Observable<S3FilesList>

    imageService = inject(ImageService)

    ngOnInit(): void {
        this.files$ = this.imageService.getFilesFromS3()
        this.filesSub$ = this.files$.subscribe(data => {
            this.files = data.files
            console.info(this.files)
        })
    }

    ngOnDestroy(): void {
        if (this.imageSub$ != null) {
            this.imageSub$.unsubscribe()
        }
        if (this.postSub$ != null) {
            this.postSub$.unsubscribe()
        }
    }

    updateLikes(value: number) {
        if (value == 1) {
            this.likes++
            this.imageService.updateLikes(this.key, 1, 0).subscribe()
        } else {
            this.dislikes++
            this.imageService.updateLikes(this.key, 0, 1).subscribe()
        }
    }

    selectImage(value: any) {
        console.info(value)
        this.key = value
        this.imageSub$ = this.imageService.getImage(this.key).subscribe(data => {
            this.image = data.image
            console.info(data)
        })

        this.postSub$ = this.imageService.getPostDetails(this.key).subscribe(data => {
            this.comments = data.comments
            this.likes = data.likes
            this.dislikes = data.dislikes
        })
    }

    resetLikes() {
        var resetSub = this.imageService.updateLikes(this.key, 0, 0).subscribe(data => {
            this.likes = data.likes
            this.dislikes = data.dislikes
        })
        setTimeout(() => {
            resetSub.unsubscribe()
        }, 1000);
    }
}
