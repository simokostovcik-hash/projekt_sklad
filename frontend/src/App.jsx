
import { useEffect, useMemo, useState } from 'react';
import {
 ArrowRight,
 ArrowUpRight,
 Check,
 Menu,
 Minus,
 Moon,
 Plus,
 ShoppingBag,
 Sun,
 Trash2,
 X
} from 'lucide-react';
import { getCoffees, submitOrder } from './api/coffeeApi';
import './App.css';

const fallback =
    'https://images.unsplash.com/photo-1447933601403-0c6688de566e?w=900';

const emptyCustomer = {
 name: '',
 email: '',
 phone: '',
 address: '',
 city: '',
 postalCode: '',
 note: ''
};

const money = value =>
    new Intl.NumberFormat('cs-CZ', {
     style: 'currency',
     currency: 'CZK',
     maximumFractionDigits: 0
    }).format(value);

export default function App() {
 const [products, setProducts] = useState([]);
 const [loading, setLoading] = useState(true);
 const [error, setError] = useState('');

 const [cart, setCart] = useState([]);
 const [drawer, setDrawer] = useState(false);
 const [menu, setMenu] = useState(false);
 const [checkout, setCheckout] = useState(false);

 const [busy, setBusy] = useState(false);
 const [notice, setNotice] = useState('');
 const [filter, setFilter] = useState('Všechny');

 const [customer, setCustomer] = useState(emptyCustomer);

 // SVĚTLÝ A TMAVÝ REŽIM
 const [theme, setTheme] = useState(() => {
  try {
   return localStorage.getItem('coffee-theme') === 'dark'
       ? 'dark'
       : 'light';
  } catch {
   return 'light';
  }
 });

 useEffect(() => {
  document.documentElement.setAttribute('data-theme', theme);

  try {
   localStorage.setItem('coffee-theme', theme);
  } catch {
   // Stránka funguje i bez localStorage.
  }
 }, [theme]);

 function toggleTheme() {
  setTheme(current => (current === 'light' ? 'dark' : 'light'));
 }

 // NAČÍTÁNÍ KÁV Z BACKENDU
 useEffect(() => {
  let active = true;

  const load = async () => {
   try {
    const data = await getCoffees();

    if (active) {
     setProducts(data);
     setError('');
    }
   } catch (err) {
    if (active) {
     setError(err.message);
    }
   } finally {
    if (active) {
     setLoading(false);
    }
   }
  };

  load();

  const timer = setInterval(load, 30000);

  return () => {
   active = false;
   clearInterval(timer);
  };
 }, []);

 const count = cart.reduce(
     (sum, item) => sum + item.quantity,
     0
 );

 const total = cart.reduce(
     (sum, item) => sum + item.quantity * Number(item.price),
     0
 );

 const types = useMemo(
     () => [
      'Všechny',
      ...new Set(products.map(p => p.type).filter(Boolean))
     ],
     [products]
 );

 const shown = products.filter(
     p => filter === 'Všechny' || p.type === filter
 );

 // PŘIDÁNÍ KÁVY DO KOŠÍKU
 function add(product) {
  const stock = Number(product.quantity ?? 0);

  if (!product.available || stock <= 0) {
   return;
  }

  setCart(items => {
   const existing = items.find(
       item => item.id === product.id
   );

   if (existing) {
    return items.map(item =>
        item.id === product.id
            ? {
             ...item,
             quantity: Math.min(
                 item.quantity + 1,
                 stock
             )
            }
            : item
    );
   }

   return [
    ...items,
    {
     ...product,
     quantity: 1
    }
   ];
  });

  setDrawer(true);
  setNotice('');
 }

 // ZMĚNA MNOŽSTVÍ V KOŠÍKU
 function adjust(id, delta) {
  setCart(items =>
      items
          .map(item => {
           if (item.id !== id) {
            return item;
           }

           const product = products.find(
               p => p.id === id
           );

           const maxQuantity = Math.max(
               0,
               Number(product?.quantity ?? item.quantity)
           );

           return {
            ...item,
            quantity: Math.max(
                0,
                Math.min(
                    item.quantity + delta,
                    maxQuantity
                )
            )
           };
          })
          .filter(item => item.quantity > 0)
  );
 }

 // ODESLÁNÍ OBJEDNÁVKY
 async function order(event) {
  event.preventDefault();

  if (!cart.length || busy) {
   return;
  }

  setBusy(true);
  setNotice('');

  try {
   const result = await submitOrder({
    customer,
    items: cart.map(item => ({
     coffeeId: item.id,
     quantity: item.quantity
    }))
   });

   // Objednávka byla úspěšně vytvořena.
   setNotice(
       `Objednávka #${result.orderId} byla úspěšně přijata.`
   );

   setCart([]);
   setCheckout(false);
   setDrawer(false);
   setCustomer({ ...emptyCustomer });

   // Aktualizace skladových zásob
   try {
    const data = await getCoffees();
    setProducts(data);
   } catch {
    // Objednávka již byla vytvořena.
    // Chyba obnovení katalogu nesmí změnit potvrzení.
   }
  } catch (err) {
   setNotice(
       err.message || 'Objednávku se nepodařilo odeslat.'
   );
  } finally {
   setBusy(false);
  }
 }

 return (
     <div className="site" id="nahoru">
      {/* HLAVIČKA */}
      <header className="header">
       <a className="logo" href="#nahoru">
        COFFEE<span>.</span>
       </a>

       <nav
           className={menu ? 'nav open' : 'nav'}
           aria-label="Hlavní navigace"
       >
        <a href="#kolekce" onClick={() => setMenu(false)}>
         Naše kávy
        </a>

        <a href="#pristup" onClick={() => setMenu(false)}>
         Náš přístup
        </a>

        <a href="#kontakt" onClick={() => setMenu(false)}>
         Kontakt
        </a>
       </nav>

       <div className="header-actions">
        {/* PŘEPÍNAČ TMAVÉHO REŽIMU */}
        <button
            type="button"
            className="theme-toggle"
            onClick={toggleTheme}
            aria-label={
             theme === 'light'
                 ? 'Zapnout tmavý režim'
                 : 'Zapnout světlý režim'
            }
            title={
             theme === 'light'
                 ? 'Tmavý režim'
                 : 'Světlý režim'
            }
        >
         {theme === 'light' ? (
             <Moon size={19} />
         ) : (
             <Sun size={19} />
         )}

         <span>
              {theme === 'light' ? 'Tmavý' : 'Světlý'}
            </span>
        </button>

        <button
            type="button"
            className="bag"
            onClick={() => setDrawer(true)}
            aria-label={`Otevřít košík, ${count} kusů`}
        >
         <ShoppingBag size={19} />
         Košík
         <span>{count}</span>
        </button>

        <button
            type="button"
            className="mobile-menu"
            onClick={() => setMenu(!menu)}
            aria-label="Přepnout menu"
        >
         {menu ? <X /> : <Menu />}
        </button>
       </div>
      </header>

      <main>
       {/* ÚVODNÍ SEKCE */}
       <section className="hero wrap">
        <div className="eyebrow">
         <span className="dot" />
         VÝBĚROVÁ KÁVA / KAŽDÝ DEN
        </div>

        <h1>
         Káva, která
         <br />
         <em>má charakter.</em>
        </h1>

        <div className="hero-lower">
         <p>
          Výjimečná zrna, poctivý původ a chuť,
          ke které se budete chtít vracet.
          Vyberte si svůj další oblíbený šálek.
         </p>

         <a className="pill dark" href="#kolekce">
          Prozkoumat kávy
          <ArrowUpRight size={19} />
         </a>

         <a
             className="round"
             href="#kolekce"
             aria-label="Přejít na kávy"
         >
          ↓
         </a>
        </div>

        <div className="hero-rule">
            <span>
              PEČLIVÝ VÝBĚR / ČERSTVÁ CHUŤ / VÁŠ RITUÁL
            </span>
         <span>SCROLL TO EXPLORE ↓</span>
        </div>
       </section>

       {/* VIZUÁLNÍ SEKCE */}
       <section className="visual-band">
        <div
            className="visual-image"
            role="img"
            aria-label="Čerstvá kávová zrna"
        />

        <div className="visual-caption">
         <span>01 / ZRNA S PŘÍBĚHEM</span>

         <strong>
          Malý moment.
          <br />
          Velký zážitek.
         </strong>

         <span>COFFEE MINIMAL — EST. 2026</span>
        </div>
       </section>

       {/* KATALOG KÁV */}
       <section className="catalog" id="kolekce">
        <div className="wrap">
         <div className="section-head">
          <div>
                <span className="kicker">
                  01 / OBJEVTE KOLEKCI
                </span>

           <h2>
            Vybráno <em>pro vás.</em>
           </h2>
          </div>

          <p>
           Od jemných ovocných tónů až po výrazné
           čokoládové chutě. Aktuální nabídka
           přímo z našeho skladu.
          </p>
         </div>

         <div
             className="filters"
             aria-label="Filtrovat kávy"
         >
          {types.map(type => (
              <button
                  key={type}
                  type="button"
                  className={
                   filter === type
                       ? 'filter active'
                       : 'filter'
                  }
                  onClick={() => setFilter(type)}
              >
               {type}
              </button>
          ))}
         </div>

         {loading && (
             <p className="state">
              Načítám nabídku…
             </p>
         )}

         {error && (
             <p className="state error" role="alert">
              {error}. Zkontrolujte spuštěný backend
              na portu 8080.
             </p>
         )}

         {!loading && !error && !shown.length && (
             <p className="state">
              Momentálně zde nejsou zveřejněné kávy.
             </p>
         )}

         <div className="product-grid">
          {shown.map((product, index) => (
              <article
                  className="product"
                  key={product.id}
              >
               <div className="product-image">
                <img
                    src={product.imageUrl || fallback}
                    alt={product.name}
                    loading="lazy"
                    onError={event => {
                     event.currentTarget.onerror = null;
                     event.currentTarget.src = fallback;
                    }}
                />

                <span className="product-index">
                      {String(index + 1).padStart(2, '0')}
                 {' / COFFEE'}
                    </span>

                {!product.available && (
                    <span className="sold">
                        VYPRODÁNO
                      </span>
                )}
               </div>

               <div className="product-body">
                <div className="product-meta">
                 {product.roastery || 'VÝBĚROVÁ KÁVA'}
                 <span>↗</span>
                </div>

                <h3>{product.name}</h3>

                <p>
                 {product.description ||
                     product.type ||
                     'Pečlivě vybraná káva pro každý den.'}
                </p>

                <div className="product-bottom">
                 <strong>
                  {money(product.price)}
                 </strong>

                 <button
                     type="button"
                     onClick={() => add(product)}
                     disabled={!product.available}
                     aria-label={
                      `Přidat ${product.name} do košíku`
                     }
                 >
                  {product.available ? (
                      <>
                       <Plus size={18} />
                       Do košíku
                      </>
                  ) : (
                      'Není skladem'
                  )}
                 </button>
                </div>
               </div>
              </article>
          ))}
         </div>
        </div>
       </section>

       {/* NÁŠ PŘÍSTUP */}
       <section
           className="philosophy"
           id="pristup"
       >
        <div className="wrap philosophy-grid">
         <div>
              <span className="kicker">
                02 / NÁŠ PŘÍSTUP
              </span>

          <h2>
           Dobrá káva.
           <br />
           <em>Bez kompromisů.</em>
          </h2>
         </div>

         <div className="philosophy-copy">
          <p>
           Věříme, že na detailech záleží.
           Proto vybíráme kávy, které mají
           svůj původ, osobnost a nezaměnitelnou chuť.
          </p>

          <div className="values">
           <div>
            <span>01</span>
            <strong>Výběr</strong>
            <small>
             Kvalitní zrna a poctivý původ
            </small>
           </div>

           <div>
            <span>02</span>
            <strong>Čerstvost</strong>
            <small>
             Káva pro váš každodenní rituál
            </small>
           </div>

           <div>
            <span>03</span>
            <strong>Jednoduchost</strong>
            <small>
             Jen to, na čem opravdu záleží
            </small>
           </div>
          </div>
         </div>
        </div>
       </section>

       {/* KONTAKTNÍ SEKCE */}
       <section
           className="cta-section"
           id="kontakt"
       >
        <div className="wrap">
            <span className="kicker">
              03 / VÁŠ DALŠÍ ŠÁLEK
            </span>

         <h2>
          Najděte si
          <br />
          <em>svou kávu.</em>
         </h2>

         <a
             href="#kolekce"
             className="pill lime"
         >
          Zpět do nabídky
          <ArrowUpRight size={19} />
         </a>
        </div>
       </section>
      </main>

      {/* PATIČKA */}
      <footer className="footer">
       <div className="wrap footer-inner">
        <a className="logo" href="#nahoru">
         COFFEE<span>.</span>
        </a>

        <span>
            VÝBĚROVÁ KÁVA S CHARAKTEREM
          </span>

        <span>
            © {new Date().getFullYear()} COFFEE MINIMAL
          </span>
       </div>
      </footer>

      {/* OZNÁMENÍ */}
      {notice && !drawer && (
          <div
              className="toast"
              role="status"
          >
           {notice}

           <button
               type="button"
               onClick={() => setNotice('')}
               aria-label="Zavřít oznámení"
           >
            <X size={16} />
           </button>
          </div>
      )}

      {/* KOŠÍK A POKLADNA */}
      {drawer && (
          <div
              className="overlay"
              onMouseDown={event => {
               if (event.target === event.currentTarget) {
                setDrawer(false);
                setCheckout(false);
               }
              }}
          >
           <aside
               className="drawer"
               aria-label="Nákupní košík"
           >
            <div className="drawer-head">
             <div>
                <span className="kicker">
                  VAŠE OBJEDNÁVKA
                </span>

              <h2>
               {checkout
                   ? 'Pokladna'
                   : 'Váš košík.'}
              </h2>
             </div>

             <button
                 type="button"
                 className="icon-btn"
                 onClick={() => {
                  setDrawer(false);
                  setCheckout(false);
                 }}
                 aria-label="Zavřít"
             >
              <X />
             </button>
            </div>

            {!checkout ? (
                <>
                 <div className="cart-list">
                  {!cart.length ? (
                      <p>
                       Košík je zatím prázdný.
                       Vyberte si kávu z naší kolekce.
                      </p>
                  ) : (
                      cart.map(item => (
                          <div
                              className="cart-item"
                              key={item.id}
                          >
                           <img
                               src={item.imageUrl || fallback}
                               alt=""
                           />

                           <div className="cart-details">
                            <strong>
                             {item.name}
                            </strong>

                            <small>
                             {money(item.price)} / ks
                            </small>

                            <div className="qty">
                             <button
                                 type="button"
                                 onClick={() =>
                                     adjust(item.id, -1)
                                 }
                                 aria-label="Odebrat kus"
                             >
                              <Minus size={14} />
                             </button>

                             <span>
                              {item.quantity}
                            </span>

                             <button
                                 type="button"
                                 onClick={() =>
                                     adjust(item.id, 1)
                                 }
                                 aria-label="Přidat kus"
                             >
                              <Plus size={14} />
                             </button>
                            </div>
                           </div>

                           <button
                               type="button"
                               className="remove"
                               onClick={() =>
                                   setCart(items =>
                                       items.filter(
                                           product =>
                                               product.id !== item.id
                                       )
                                   )
                               }
                               aria-label="Odebrat produkt"
                           >
                            <Trash2 size={18} />
                           </button>
                          </div>
                      ))
                  )}
                 </div>

                 <div className="drawer-bottom">
                  <div className="total">
                   <span>Celkem</span>
                   <strong>
                    {money(total)}
                   </strong>
                  </div>

                  <button
                      type="button"
                      className="checkout-btn"
                      disabled={!cart.length}
                      onClick={() => {
                       setNotice('');
                       setCheckout(true);
                      }}
                  >
                   Pokračovat k objednávce
                   <ArrowRight size={19} />
                  </button>

                  <button
                      type="button"
                      className="text-btn"
                      onClick={() => setDrawer(false)}
                  >
                   Pokračovat v nákupu
                  </button>
                 </div>
                </>
            ) : (
                <form
                    className="checkout-form"
                    onSubmit={order}
                >
                 <button
                     type="button"
                     className="back"
                     onClick={() => {
                      setNotice('');
                      setCheckout(false);
                     }}
                 >
                  ← Zpět do košíku
                 </button>

                 <p>
                  Vyplňte kontaktní a doručovací údaje.
                  Objednávku potvrdíme po odeslání.
                 </p>

                 {[
                  ['name', 'Jméno a příjmení'],
                  ['email', 'E-mail'],
                  ['phone', 'Telefon'],
                  ['address', 'Ulice a číslo'],
                  ['city', 'Město'],
                  ['postalCode', 'PSČ']
                 ].map(([key, label]) => (
                     <label key={key}>
                      {label}

                      <input
                          required
                          type={
                           key === 'email'
                               ? 'email'
                               : key === 'phone'
                                   ? 'tel'
                                   : 'text'
                          }
                          maxLength={
                           key === 'email' ? 150 : 120
                          }
                          value={customer[key]}
                          onChange={event =>
                              setCustomer(current => ({
                               ...current,
                               [key]: event.target.value
                              }))
                          }
                      />
                     </label>
                 ))}

                 <label>
                  Poznámka (nepovinná)

                  <textarea
                      maxLength={500}
                      value={customer.note}
                      onChange={event =>
                          setCustomer(current => ({
                           ...current,
                           note: event.target.value
                          }))
                      }
                  />
                 </label>

                 <div className="total">
                  <span>Celkem</span>
                  <strong>
                   {money(total)}
                  </strong>
                 </div>

                 {notice && (
                     <p
                         className="form-error"
                         role="alert"
                     >
                      {notice}
                     </p>
                 )}

                 <button
                     type="submit"
                     className="checkout-btn"
                     disabled={busy}
                 >
                  {busy
                      ? 'Odesílám…'
                      : 'Odeslat objednávku'}

                  <Check size={19} />
                 </button>
                </form>
            )}
           </aside>
          </div>
      )}
     </div>
 );
}
