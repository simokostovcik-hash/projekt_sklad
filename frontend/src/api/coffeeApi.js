
export async function getCoffees() {
 const response = await fetch('/api/coffees');

 if (!response.ok) {
  throw new Error(
      `Katalog se nepodařilo načíst (${response.status})`
  );
 }

 return response.json();
}

export async function submitOrder(payload) {
 const response = await fetch('/api/orders', {
  method: 'POST',
  headers: {
   'Content-Type': 'application/json'
  },
  body: JSON.stringify(payload)
 });

 const data = await response.json().catch(() => ({}));

 if (!response.ok) {
  throw new Error(
      data.error ||
      'Objednávku se nepodařilo odeslat. Zkuste to znovu.'
  );
 }

 return data;
}

export async function completeDemoPayment(orderId, paymentToken) {
 const response = await fetch('/api/payments/demo/complete', {
  method: 'POST',
  headers: {
   'Content-Type': 'application/json'
  },
  body: JSON.stringify({
   orderId,
   paymentToken
  })
 });

 const data = await response.json().catch(() => ({}));

 if (!response.ok) {
  throw new Error(
      data.error || 'Testovací platba se nezdařila.'
  );
 }

 return data;
}

export async function downloadInvoice(orderId, paymentToken) {
 if (!orderId || !paymentToken) {
  throw new Error(
      'Chybí číslo objednávky nebo platební token.'
  );
 }

 const response = await fetch(
     `/api/orders/${orderId}/invoice?paymentToken=${encodeURIComponent(paymentToken)}`
 );

 if (!response.ok) {
  throw new Error(
      `Fakturu se nepodařilo stáhnout (${response.status}).`
  );
 }

 const blob = await response.blob();
 const url = URL.createObjectURL(blob);

 const link = document.createElement('a');
 link.href = url;
 link.download = `faktura-${orderId}.pdf`;

 document.body.appendChild(link);
 link.click();
 link.remove();

 setTimeout(() => URL.revokeObjectURL(url), 1000);
}
