export async function getCoffees() {
 const response = await fetch('/api/coffees');
 if (!response.ok) throw new Error(`Katalog se nepodařilo načíst (${response.status})`);
 return response.json();
}
export async function submitOrder(payload) {
 const response = await fetch('/api/orders', {method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(payload)});
 const data = await response.json().catch(()=>({}));
 if(!response.ok) throw new Error(data.error || 'Objednávku se nepodařilo odeslat. Zkuste to znovu.');
 return data;
}

export async function completeDemoPayment(orderId, paymentToken) {
 const response = await fetch('/api/payments/demo/complete', {
  method: 'POST', headers: {'Content-Type': 'application/json'},
  body: JSON.stringify({orderId, paymentToken})
 });
 const data = await response.json().catch(() => ({}));
 if (!response.ok) throw new Error(data.error || 'Testovací platba se nezdařila.');
 return data;
}
