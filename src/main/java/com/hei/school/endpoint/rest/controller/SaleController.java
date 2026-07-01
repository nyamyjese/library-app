package com.hei.school.endpoint.rest.controller;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {

  private final SaleService saleService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public SaleResponse create(@RequestBody @Valid CreateSaleRequest request) {
    return saleService.create(request);
  }

  @GetMapping
  public List<SaleResponse> getAll(
      @RequestParam(required = false) UUID customerId,
      @RequestParam(required = false) UUID libraryId,
      @RequestParam(required = false) Instant from,
      @RequestParam(required = false) Instant to) {
    return saleService.getAll(customerId, libraryId, from, to);
  }

  @GetMapping("/{id}")
  public SaleResponse getById(@PathVariable UUID id) {
    return saleService.getById(id);
  }

  @PutMapping("/{id}")
  public SaleResponse update(@PathVariable UUID id, @RequestBody @Valid UpdateSaleRequest request) {
    return saleService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    saleService.delete(id);
  }
}
