import { TestBed } from '@angular/core/testing';

import { TagDefinitionService } from './tag-definition.service';

describe('TagDefinitionService', () => {
  let service: TagDefinitionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TagDefinitionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
