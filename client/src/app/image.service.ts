import { HttpClient } from "@angular/common/http";
import { Injectable, OnInit, inject } from "@angular/core";
import { Observable } from "rxjs";

const URL = '/image'

@Injectable()
export class ImageService implements OnInit {

    http = inject(HttpClient)

    image = ''
    contentType = ''

    ngOnInit(): void {
        
    }

    upload(comments: string, file: File): Observable<any> {
        const fdata = new FormData
        fdata.set('comments', comments)
        fdata.set('imageFile', file)

        return this.http.post('http://localhost:8080/upload', fdata)
    }

}