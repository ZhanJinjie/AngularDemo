import axios from 'axios';
import { from, Observable } from "rxjs"
import { map } from 'rxjs/operators';
import QueryOptions from '../queries/query-options';
import { Page } from '../queries/page';
declare const config: any;
export class HttpService<T extends { id?: string }> {

  public static BaseUrl = config.baseUrl
  /**
   * 把相对地址转化为绝对地址
   * @param url 相对地址
   * @returns 返回绝对地址
   */
  public url(url: string, options?: QueryOptions) {
    let rurl = url;
    if (options) {
      rurl += "/" + options.page + "/" + options.size;
      let sortReqs = "none";
      let filterReqs = "none"
      if (options.sorts) {
        sortReqs = encodeURI(JSON.stringify(options.sorts));
      }
      if (options.filters) {
        filterReqs = encodeURI(JSON.stringify(options.filters));
      }
      rurl += "/" + sortReqs + "/" + filterReqs;
    }
    return HttpService.BaseUrl + rurl;
  }
  public getRequest(url: string): Observable<any> {
    return from(axios.get(url).then(response => response.data))
    // return axios.get(url).then(response => response.data);
  }

  public postRequest(url: string, data?: any): Observable<any> {
    return from(axios.post(url, data).then(response => response.data));
    // return axios.post(url, data).then(response => response.data);
  }

  public putRequest(url: string, data?: any): Observable<any> {
    return from(axios.put(url, data).then(response => response.data));
    // return axios.put(url, data).then(response => response.data);
  }

  public deleteRequest(url: string): Observable<any> {
    return from(axios.delete(url).then(response => response.data));
    // return axios.delete(url).then(response => response.data);
  }

  protected requestBase = "";

  public list(options?: QueryOptions): Observable<Page<T>> {
    const rurl = this.requestBase;
    return this.getRequest(this.url(rurl, options))
      .pipe(map(val => {
        if (null == val) {
          return null;
        }


        if (val.content) {
          return val;
        }
        const page: Page<T> = {
          content: val as T[],
          totalElements: (val as T[]).length
        }
        return page;
      }));
  }

  public one(id: string): Observable<T> {
    return this.getRequest(this.url(this.requestBase + "/" + id));
  }

  public create(entity: T): Observable<string> {
    return this.postRequest(this.url(this.requestBase), entity);
  }
  public update(id: any, entity: T): Observable<void> {
    return this.putRequest(this.url(this.requestBase + "/" + id), entity);
  }

  public delete(id: string): Observable<void> {
    return this.deleteRequest(this.url(this.requestBase + "/" + id));
  }
}