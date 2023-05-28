import { Component, OnInit, inject } from '@angular/core';
import { Observable, firstValueFrom } from 'rxjs';
import { ImageService } from '../image.service';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-image',
  templateUrl: './image.component.html',
  styleUrls: ['./image.component.css']
})
export class ImageComponent implements OnInit {

    image$!: Observable<void>
    image = ''
    form!: FormGroup
    imageFile!: File

    imageService = inject(ImageService)
    router = inject(Router)
    fb = inject(FormBuilder)

    ngOnInit(): void {
        if (!this.imageService.image) {
            this.router.navigate([ '/' ])
        } else {
            this.image = this.imageService.image
        }

        this.imageFile = this.dataURLtoFile(this.image, 'imageFile')

        this.form = this.fb.group({
            imageFile: this.fb.control<File | null>(this.imageFile, [ Validators.required ]),
            comments: this.fb.control<string>('', [ Validators.required ]),
            likes: this.fb.control<number>(0, [ Validators.min(0)]),
            dislikes: this.fb.control<number>(0, [Validators.max(0)])
        })
    }

    upload() {
        const data = this.form.value
        console.info(data)
        // Must subscribe if using Observable, while a Promise will always execute
        this.imageService.upload(data['comments'], this.imageFile).subscribe()
        this.router.navigate([ '/' ])
    }

    updateLikes(value: number) {
        if (value == 1) {
            this.imageService.updateLikes(1, 0).subscribe()
        } else {
            this.imageService.updateLikes(0, 1).subscribe()
        }
    }


    dataURLtoFile(dataurl: string, filename: string) {
        var arr = dataurl.split(','),
            mime = arr[0].match(/:(.*?);/)![1],
            bstr = atob(arr[arr.length - 1]), 
            n = bstr.length, 
            u8arr = new Uint8Array(n);
        while(n--){
            u8arr[n] = bstr.charCodeAt(n);
        }
        return new File([u8arr], filename, {type:mime});
    }

}
