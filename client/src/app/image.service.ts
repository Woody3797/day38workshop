import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable, OnInit, inject } from "@angular/core";
import { Observable } from "rxjs";
import { ImageDataAsString, Post, S3FilesList } from "./model";

const URL = '/image'

@Injectable()
export class ImageService implements OnInit {

    http = inject(HttpClient)

    image = ''
    contentType = ''
    likes = 0
    dislikes = 0

    ngOnInit(): void {
        
    }

    upload(comments: string, file: File): Observable<any> {
        const fdata = new FormData
        fdata.set('comments', comments)
        fdata.set('imageFile', file)

        return this.http.post('http://localhost:8080/upload', fdata)
    }

    updateLikes(key: string, likes: number, dislikes: number): Observable<any> {
        const likesCount = new FormData
        likesCount.set('key', key)
        likesCount.set('likes', likes.toString())
        likesCount.set('dislikes', dislikes.toString())

        return this.http.post<any>('http://localhost:8080/updatelikes', likesCount)
    }

    getImage(key: string): Observable<ImageDataAsString> {
        const params = new HttpParams().set('key', key)
        return this.http.get<ImageDataAsString>('http://localhost:8080/getimage', {params})
    }

    getPostDetails(key: string): Observable<Post> {
        const params = new HttpParams().set('key', key)
        return this.http.get<Post>('http://localhost:8080/getdetails', {params})
    }

    getFilesFromS3(): Observable<S3FilesList> {
        return this.http.get<S3FilesList>('http://localhost:8080/getfiles')
    }
}