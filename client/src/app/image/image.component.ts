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
            comments: this.fb.control<string>('', [ Validators.required ])
        })
    }

    upload() {
        const data = this.form.value
        console.info(data)
        firstValueFrom(this.imageService.upload(data['comments'], this.imageFile))
        .then(result => {
            alert('uploaded')
            this.form.reset()
          })
          .catch(err => {
            alert(JSON.stringify(err))
          })
        
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
